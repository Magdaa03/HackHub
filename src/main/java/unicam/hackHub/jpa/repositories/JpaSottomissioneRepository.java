package unicam.hackHub.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unicam.hackHub.entities.hackathon.Sottomissione;
import unicam.hackHub.repositories.SottomissioneRepository;

@Repository
public interface JpaSottomissioneRepository
        extends JpaRepository<Sottomissione, String>, SottomissioneRepository {

    @Override
    @Query("""
            select case when count(s) > 0 then true else false end
            from Sottomissione s
            where s.team.id = :teamId and s.hackathon.id = :hackathonId
            """)
    boolean existsByTeamIdAndHackathonId(
            @Param("teamId") String teamId,
            @Param("hackathonId") String hackathonId);
}