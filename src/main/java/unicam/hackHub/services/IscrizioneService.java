package unicam.hackHub.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import unicam.hackHub.entities.hackathon.Hackathon;
import unicam.hackHub.entities.hackathon.Iscrizione;
import unicam.hackHub.entities.team.Team;
import unicam.hackHub.entities.utente.Utente;
import unicam.hackHub.repositories.HackathonRepository;
import unicam.hackHub.repositories.IscrizioneRepository;
import unicam.hackHub.repositories.TeamRepository;
import unicam.hackHub.repositories.UtenteRepository;

@Service
public class IscrizioneService {

    private final IscrizioneRepository iscrizioneRepository;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final UtenteRepository utenteRepository;

    public IscrizioneService(
            IscrizioneRepository iscrizioneRepository,
            TeamRepository teamRepository,
            HackathonRepository hackathonRepository,
            UtenteRepository utenteRepository) {
        this.iscrizioneRepository = iscrizioneRepository;
        this.teamRepository = teamRepository;
        this.hackathonRepository = hackathonRepository;
        this.utenteRepository = utenteRepository;
    }

    @Transactional
    public Iscrizione iscriviTeam(
            String idHackathon, String emailUtente, String idTeam) {
        Hackathon hackathon = hackathonRepository.findById(idHackathon)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hackathon non trovato."));

        Team team = teamRepository.findById(idTeam)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Team non trovato."));

        Utente utente = utenteRepository.findByEmail(emailUtente)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utente non trovato."));

        boolean utenteNelTeam = team.getMembri().stream()
                .anyMatch(membro -> membro.getId().equals(utente.getId()));

        if (!utenteNelTeam) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Solo un membro del team può iscriverlo.");
        }

        if (!hackathon.getStato().consenteIscrizioni()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Le iscrizioni all'hackathon sono chiuse.");
        }

        Date oggi = Date.from(LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        if (hackathon.getScadenzaIscrizioni().before(oggi)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "La scadenza delle iscrizioni è passata.");
        }

        if (iscrizioneRepository.existsByTeamIdAndHackathonId(idTeam, idHackathon)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Il team è già iscritto a questo hackathon.");
        }

        if (team.getMembri().size() > hackathon.getMaxTeamSize()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Il team supera la dimensione massima consentita.");
        }

        return iscrizioneRepository.save(new Iscrizione(team, hackathon));
    }
}