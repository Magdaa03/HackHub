package unicam.hackHub.jpa.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicam.hackHub.entities.Team;
import unicam.hackHub.repositories.TeamRepository;

@Repository
public interface JpaTeamRepository
        extends JpaRepository<Team, String>, TeamRepository {

    @Override
    default Team saveTeam(Team team) {
        return save(team);
    }

    @Override
    Optional<Team> findByNome(String nome);

    @Override
    boolean existsByMembri_Id(String utenteId);
}