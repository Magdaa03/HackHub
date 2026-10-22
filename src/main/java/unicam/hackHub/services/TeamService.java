package unicam.hackHub.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unicam.hackHub.entities.Invito;
import unicam.hackHub.entities.StatoInvito;
import unicam.hackHub.entities.Team;
import unicam.hackHub.entities.Utente;
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
    public Team creaTeam(String id, String nome, Utente creatore) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome del team è obbligatorio.");
        }

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'ID del team è obbligatorio.");
        }

        if (creatore == null || creatore.getId() == null) {
            throw new IllegalArgumentException("L'utente creatore è obbligatorio.");
        }

        String nomeNormalizzato = nome.trim();
        if (teamRepository.findByName(nomeNormalizzato).isPresent()) {
            throw new IllegalArgumentException("Esiste già un team con questo nome.");
        }

        if (teamRepository.existsByMembri_Id(creatore.getId())) {
            throw new IllegalStateException("L'utente appartiene già a un team.");
        }

        Team team = new Team(id, nomeNormalizzato);
        team.aggiungiMembro(creatore);

        return teamRepository.save(team);
    }

    @Transactional
    public Invito inviaInvito(String id, String emailDestinatario) {
        if (emailDestinatario == null || emailDestinatario.isBlank()) {
            throw new IllegalArgumentException("L'email del destinatario è obbligatoria.");
        }

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato."));
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

    @Transactional
    public void gestisciRispostaInvito(String invitoId, boolean accetta) {
        Invito invito = invitoRepository.findById(invitoId)
                .orElseThrow(() -> new IllegalArgumentException("Invito non trovato."));

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
            invito.accetta();
        } else {
            invito.rifiuta();
        }

        invitoRepository.save(invito);
    }

    @Transactional
    public void abbandonaTeam(String teamId, String utenteId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato."));
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));

        boolean membroDelTeam = team.getMembri().stream()
                .anyMatch(membro -> membro.getId().equals(utenteId));
        if (!membroDelTeam) {
            throw new IllegalStateException("L'utente non appartiene a questo team.");
        }

        team.rimuoviMembro(utente);
        teamRepository.save(team);
    }
}
