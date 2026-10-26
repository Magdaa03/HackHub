package unicam.hackHub.services;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unicam.hackHub.entities.team.Invito;
import unicam.hackHub.entities.team.StatoInvito;
import unicam.hackHub.entities.team.Team;
import unicam.hackHub.entities.utente.Utente;
import unicam.hackHub.repositories.InvitoRepository;
import unicam.hackHub.repositories.TeamRepository;
import unicam.hackHub.repositories.UtenteRepository;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final InvitoRepository invitoRepository;
    private final UtenteRepository utenteRepository;

    public TeamService(
            TeamRepository teamRepository,
            InvitoRepository invitoRepository,
            UtenteRepository utenteRepository) {
        this.teamRepository = teamRepository;
        this.invitoRepository = invitoRepository;
        this.utenteRepository = utenteRepository;
    }

    @Transactional
    public Team creaTeam(String nome, String emailCreatore) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome del team è obbligatorio.");
        }

        Utente creatore = utenteRepository.findByEmail(emailCreatore)
                .orElseThrow(() -> new IllegalArgumentException("Utente autenticato non trovato."));

        String nomeNormalizzato = nome.trim();
        if (teamRepository.findByName(nomeNormalizzato).isPresent()) {
            throw new IllegalArgumentException("Esiste già un team con questo nome.");
        }

        if (teamRepository.existsByMembri_Id(creatore.getId())) {
            throw new IllegalStateException("L'utente appartiene già a un team.");
        }

        Team team = new Team(UUID.randomUUID().toString(), nomeNormalizzato);
        team.aggiungiMembro(creatore);

        return teamRepository.save(team);
    }

    @Transactional
    public Invito inviaInvito(String id, String emailMittente, String emailDestinatario) {
        if (emailDestinatario == null || emailDestinatario.isBlank()) {
            throw new IllegalArgumentException("L'email del destinatario è obbligatoria.");
        }

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato."));
        Utente mittente = utenteRepository.findByEmail(emailMittente)
                .orElseThrow(() -> new IllegalArgumentException("Utente autenticato non trovato."));
        boolean mittenteNelTeam = team.getMembri().stream()
                .anyMatch(membro -> membro.getId().equals(mittente.getId()));
        if (!mittenteNelTeam) {
            throw new IllegalStateException("Solo un membro del team può inviare inviti.");
        }
        Utente destinatario = utenteRepository.findByEmail(emailDestinatario.trim())
                .orElseThrow(() -> new IllegalArgumentException("Utente destinatario non trovato."));

        if (teamRepository.existsByMembri_Id(destinatario.getId())) {
            throw new IllegalStateException("L'utente appartiene già a un team.");
        }

        boolean invitoGiaPendente = invitoRepository
                .findByDestinatarioAndStato(destinatario, StatoInvito.PENDENTE)
                .stream()
                .anyMatch(invito -> invito.getMittente().getId().equals(team.getId()));
        if (invitoGiaPendente) {
            throw new IllegalStateException("Esiste già un invito pendente per questo utente.");
        }

        Invito invito = team.creaInvito(destinatario);
        return invitoRepository.save(invito);
    }

    @Transactional(readOnly = true)
    public List<Invito> listaInvitiRicevuti(String emailUtente) {
        Utente destinatario = utenteRepository.findByEmail(emailUtente)
                .orElseThrow(() -> new IllegalArgumentException("Utente autenticato non trovato."));
        return invitoRepository.findByDestinatarioAndStato(destinatario, StatoInvito.PENDENTE);
    }

    @Transactional
    public void gestisciRispostaInvito(String invitoId, String emailDestinatario, boolean accetta) {
        Invito invito = invitoRepository.findById(invitoId)
                .orElseThrow(() -> new IllegalArgumentException("Invito non trovato."));

        if (!invito.getDestinatario().getEmail().equalsIgnoreCase(emailDestinatario)) {
            throw new IllegalStateException("Solo il destinatario dell'invito può rispondere.");
        }

        if (invito.getStato() != StatoInvito.PENDENTE) {
            throw new IllegalStateException("L'invito non è più pendente.");
        }

        if (accetta) {
            Utente destinatario = invito.getDestinatario();
            if (teamRepository.existsByMembri_Id(destinatario.getId())) {
                throw new IllegalStateException("L'utente appartiene già a un team.");
            }

            Team team = invito.getMittente();
            team.aggiungiMembro(destinatario);
            teamRepository.save(team);
            destinatario.accettaInvito(invito);
        } else {
            invito.rifiuta();
        }

        invitoRepository.save(invito);
    }

    @Transactional
    public void abbandonaTeam(String teamId, String emailUtente) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato."));
        Utente utente = utenteRepository.findByEmail(emailUtente)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        boolean membroDelTeam = team.getMembri().stream()
                .anyMatch(membro -> membro.getId().equals(utente.getId()));
        if (!membroDelTeam) {
            throw new IllegalStateException("L'utente non appartiene a questo team.");
        }

        team.rimuoviMembro(utente);
        teamRepository.save(team);
    }
}
