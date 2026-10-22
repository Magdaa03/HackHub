package unicam.hackHub.jpa.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicam.hackHub.entities.Utente;
import unicam.hackHub.repositories.UtenteRepository;

@Repository
public interface JpaUtenteRepository extends JpaRepository<Utente, String>, UtenteRepository {

    @Override
    Optional<Utente> findByEmail(String email);
}
