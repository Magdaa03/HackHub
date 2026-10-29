package unicam.hackHub.repositories;

import unicam.hackHub.entities.hackathon.Iscrizione;

public interface IscrizioneRepository {

    Iscrizione save(Iscrizione iscrizione);

    boolean existsByTeamIdAndHackathonId(String teamId, String hackathonId);
}