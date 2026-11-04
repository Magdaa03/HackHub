package unicam.hackHub.services;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import unicam.hackHub.entities.hackathon.Hackathon;
import unicam.hackHub.entities.hackathon.RichiestaSupporto;
import unicam.hackHub.entities.staff.Mentore;
import unicam.hackHub.entities.staff.ProfiloStaff;
import unicam.hackHub.entities.team.Team;
import unicam.hackHub.entities.utente.Utente;
import unicam.hackHub.repositories.HackathonRepository;
import unicam.hackHub.repositories.IscrizioneRepository;
import unicam.hackHub.repositories.ProfiloStaffRepository;
import unicam.hackHub.repositories.RichiestaSupportoRepository;
import unicam.hackHub.repositories.TeamRepository;
import unicam.hackHub.repositories.UtenteRepository;
import unicam.hackHub.state.StatoRichiestaSupporto;

@Service
public class RichiestaSupportoService {

    private final RichiestaSupportoRepository richiestaSupportoRepository;
    private final UtenteRepository utenteRepository;
    private final ICalendario calendario;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final ProfiloStaffRepository profiloStaffRepository;
    private final IscrizioneRepository iscrizioneRepository;

    public RichiestaSupportoService(
            RichiestaSupportoRepository richiestaSupportoRepository,
            UtenteRepository utenteRepository,
            ICalendario calendario,
            TeamRepository teamRepository,
            HackathonRepository hackathonRepository,
            ProfiloStaffRepository profiloStaffRepository,
            IscrizioneRepository iscrizioneRepository) {
        this.richiestaSupportoRepository = richiestaSupportoRepository;
        this.utenteRepository = utenteRepository;
        this.calendario = calendario;
        this.teamRepository = teamRepository;
        this.hackathonRepository = hackathonRepository;
        this.profiloStaffRepository = profiloStaffRepository;
        this.iscrizioneRepository = iscrizioneRepository;
    }

    @Transactional
    public RichiestaSupporto creaRichiestaSupporto(
            String hackathonId,
            String slot,
            String teamId,
            String mentoreId,
            String descrizione) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Team non trovato."));

        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hackathon non trovato."));

        if (!iscrizioneRepository.existsByTeamIdAndHackathonId(teamId, hackathonId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Il team non è iscritto a questo hackathon.");
        }

        verificaMembroTeam(team);

        ProfiloStaff profilo = profiloStaffRepository.findById(mentoreId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Mentore non trovato."));

        if (!(profilo instanceof Mentore mentore)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Il profilo selezionato non è un mentore.");
        }

        if (mentore.getHackathon() == null
                || !mentore.getHackathon().getId().equals(hackathon.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Il mentore non è assegnato a questo hackathon.");
        }

        if (slot == null || slot.isBlank()
                || descrizione == null || descrizione.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Slot e descrizione sono obbligatori.");
        }

        String idEvento = prenotaSlot(slot, team, hackathon, descrizione);

        try {
            RichiestaSupporto richiesta = new RichiestaSupporto(
                    slot, descrizione, idEvento, team, hackathon, mentore);
            return richiestaSupportoRepository.save(richiesta);
        } catch (RuntimeException e) {
            // Il rollback del database non cancella l'evento sul calendario esterno.
            calendario.annullaCall(idEvento);
            throw e;
        }
    }

    @Transactional
    public RichiestaSupporto gestisciRispostaRichiesta(
            String richiestaId, boolean accetta) {

        RichiestaSupporto richiesta = richiestaSupportoRepository.findById(richiestaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Richiesta di supporto non trovata."));

        verificaMentoreAutenticato(richiesta.getMentore());

        if (richiesta.getStato() != StatoRichiestaSupporto.PENDENTE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "La richiesta è già stata gestita.");
        }

        if (accetta) {
            richiesta.accettaCall(richiesta.getSlot());
        } else {
            try {
                calendario.annullaCall(richiesta.getIdEventoCalendario());
            } catch (IllegalStateException e) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY, e.getMessage());
            }
            richiesta.rifiutaCall(richiesta.getSlot());
        }

        return richiestaSupportoRepository.save(richiesta);
    }

    @Transactional(readOnly = true)
    public List<RichiestaSupporto> richiesteRicevuteDalMentore() {
        String email = emailAutenticato();
        boolean mentore = profiloStaffRepository.findAll().stream()
                .anyMatch(profilo -> profilo instanceof Mentore
                        && profilo.getEmail().equalsIgnoreCase(email));
        if (!mentore) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Solo un mentore può consultare le richieste ricevute.");
        }
        return richiestaSupportoRepository.findByMentore_Utente_EmailIgnoreCase(email);
    }

    private String prenotaSlot(String slot, Team team, Hackathon hackathon, String descrizione) {
        String titolo = "Call di supporto - " + team.getNome() + " (" + hackathon.getNome() + ")";
        try {
            return calendario.prenotaCall(slot, titolo, descrizione);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    private void verificaMembroTeam(Team team) {
        Utente utente = utenteRepository.findByEmail(emailAutenticato())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utente non trovato."));

        boolean membro = team.getMembri().stream()
                .anyMatch(membroTeam -> membroTeam.getId().equals(utente.getId()));

        if (!membro) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Solo un membro del team può richiedere supporto.");
        }
    }

    private void verificaMentoreAutenticato(Mentore mentore) {
        if (!mentore.getEmail().equalsIgnoreCase(emailAutenticato())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Solo il mentore assegnato può rispondere.");
        }
    }

    private String emailAutenticato() {
        Authentication autenticazione =
                SecurityContextHolder.getContext().getAuthentication();

        if (autenticazione == null
                || !autenticazione.isAuthenticated()
                || "anonymousUser".equals(autenticazione.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "È necessario effettuare l'accesso.");
        }

        return autenticazione.getName();
    }
}