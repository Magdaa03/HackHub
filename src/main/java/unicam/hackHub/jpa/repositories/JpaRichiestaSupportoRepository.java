package unicam.hackHub.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicam.hackHub.entities.hackathon.RichiestaSupporto;
import unicam.hackHub.repositories.RichiestaSupportoRepository;

@Repository
public interface JpaRichiestaSupportoRepository
        extends JpaRepository<RichiestaSupporto, String>, RichiestaSupportoRepository {
}