package unicam.hackHub.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicam.hackHub.entities.hackathon.Hackathon;
import unicam.hackHub.repositories.HackathonRepository;

@Repository
public interface JpaHackathonRepository
        extends JpaRepository<Hackathon, String>, HackathonRepository {
}
