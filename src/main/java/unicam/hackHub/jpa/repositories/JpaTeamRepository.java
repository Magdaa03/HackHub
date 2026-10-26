package unicam.hackHub.jpa.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unicam.hackHub.entities.team.Team;
import unicam.hackHub.repositories.TeamRepository;

@Repository
public interface JpaTeamRepository extends JpaRepository<Team, String>, TeamRepository {

    @Override
    @Query("select t from Team t where t.nome = :nome")
    Optional<Team> findByName(@Param("nome") String nome);

    @Override
    boolean existsByMembri_Id(String utenteId);
}
