package unicam.hackHub.services;

import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import unicam.hackHub.entities.hackathon.Hackathon;
import unicam.hackHub.entities.hackathon.Sottomissione;
import unicam.hackHub.entities.team.Team;
import unicam.hackHub.entities.utente.Utente;
import unicam.hackHub.repositories.HackathonRepository;
import unicam.hackHub.repositories.IscrizioneRepository;
import unicam.hackHub.repositories.SottomissioneRepository;
import unicam.hackHub.repositories.TeamRepository;
import unicam.hackHub.repositories.UtenteRepository;

@Service
public class SottomissioneService {

    private final HackathonRepository hackathonRepository;
    private final IscrizioneRepository iscrizioneRepository;
    private final SottomissioneRepository sottomissioneRepository;
    private final TeamRepository teamRepository;
    private final UtenteRepository utenteRepository;

    public SottomissioneService(
            HackathonRepository hackathonRepository,
            IscrizioneRepository iscrizioneRepository,
            SottomissioneRepository sottomissioneRepository,
            TeamRepository teamRepository,
            UtenteRepository utenteRepository) {
        this.hackathonRepository = hackathonRepository;
        this.iscrizioneRepository = iscrizioneRepository;
        this.sottomissioneRepository = sottomissioneRepository;
        this.teamRepository = teamRepository;
        this.utenteRepository = utenteRepository;
    }

    @Transactional
    public Sottomissione inviaSottomissione(
            String idHackathon, String idTeam, String emailUtente, String descrizione) {
        Hackathon hackathon = hackathonRepository.findById(idHackathon)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hackathon non trovato."));

        Team team = teamRepository.findById(idTeam)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Team non trovato."));

        verificaMembro(team, emailUtente);

        if (!iscrizioneRepository.existsByTeamIdAndHackathonId(idTeam, idHackathon)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Il team non è iscritto a questo hackathon.");
        }

        if (!hackathon.getStato().consenteInvioSottomissione()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "L'hackathon non consente l'invio delle sottomissioni.");
        }

        verificaScadenza(hackathon);

        if (sottomissioneRepository.existsByTeamIdAndHackathonId(idTeam, idHackathon)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Il team ha già inviato una sottomissione.");
        }

        verificaDescrizione(descrizione);
        return sottomissioneRepository.save(new Sottomissione(descrizione, team, hackathon));
    }

    @Transactional
    public Sottomissione aggiornaSottomissione(
            String idSottomissione, String emailUtente, String descrizione) {
        Sottomissione sottomissione = sottomissioneRepository.findById(idSottomissione)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sottomissione non trovata."));

        verificaMembro(sottomissione.getTeam(), emailUtente);

        Hackathon hackathon = sottomissione.getHackathon();
        if (!hackathon.getStato().consenteAggiornamentoSottomissione()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "L'hackathon non consente modifiche alla sottomissione.");
        }

        verificaScadenza(hackathon);
        verificaDescrizione(descrizione);

        sottomissione.aggiorna(descrizione);
        return sottomissioneRepository.save(sottomissione);
    }

    private void verificaMembro(Team team, String emailUtente) {
        Utente utente = utenteRepository.findByEmail(emailUtente)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utente non trovato."));

        boolean membro = team.getMembri().stream()
                .anyMatch(m -> m.getId().equals(utente.getId()));

        if (!membro) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Solo un membro del team può gestire la sottomissione.");
        }
    }

    private void verificaScadenza(Hackathon hackathon) {
        LocalDate dataFine = hackathon.getDataFine().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        if (LocalDate.now().isAfter(dataFine)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "La scadenza per le sottomissioni è passata.");
        }
    }

    private void verificaDescrizione(String descrizione) {
        if (descrizione == null || descrizione.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La descrizione della sottomissione è obbligatoria.");
        }
    }
}