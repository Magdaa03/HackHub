package unicam.hackHub.repositories;

import java.util.Optional;
import unicam.hackHub.entities.Team;

public interface TeamRepository {
    Team saveTeam(Team team);
    Optional<Team> findById(String id);
    Optional<Team> findByNome(String nome);
    boolean existsByMembri_Id(String utenteId);
}