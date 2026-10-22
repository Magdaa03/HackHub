package unicam.hackHub.services;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unicam.hackHub.entities.Team;
import unicam.hackHub.entities.Utente;
import unicam.hackHub.repositories.TeamRepository;
import unicam.hackHub.repositories.UtenteRepository;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UtenteRepository utenteRepository;

    public TeamService(TeamRepository teamRepository, UtenteRepository utenteRepository) {
        this.teamRepository = teamRepository;
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
        if (teamRepository.findByNome(nomeNormalizzato).isPresent()) {
            throw new IllegalArgumentException("Esiste già un team con questo nome.");
        }

        if (teamRepository.existsByMembri_Id(creatore.getId())) {
            throw new IllegalStateException("L'utente appartiene già a un team.");
        }

        Team team = new Team(id, nomeNormalizzato);
        team.aggiungiMembro(creatore);

        return teamRepository.saveTeam(team);
    }

    /** Adattatore temporaneo per la richiesta REST, finché non è disponibile l'autenticazione. */
    @Transactional
    public Team creaTeamDaRichiesta(String nome, String idCreatore) {
        Utente creatore = utenteRepository.findById(idCreatore)
                .orElseThrow(() -> new IllegalArgumentException("Utente creatore non trovato."));

        return creaTeam(UUID.randomUUID().toString(), nome, creatore);
    }
}
