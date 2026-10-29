package unicam.hackHub.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unicam.hackHub.entities.hackathon.Iscrizione;
import unicam.hackHub.repositories.IscrizioneRepository;

@Repository
public interface JpaIscrizioneRepository
        extends JpaRepository<Iscrizione, String>, IscrizioneRepository {

    @Override
    @Query("""
            select case when count(i) > 0 then true else false end
            from Iscrizione i
            where i.team.id = :teamId and i.hackathon.id = :hackathonId
            """)
    boolean existsByTeamIdAndHackathonId(
            @Param("teamId") String teamId,
            @Param("hackathonId") String hackathonId);
}