package unicam.hackHub.repositories;

import java.util.Optional;
import unicam.hackHub.entities.Utente;

public interface UtenteRepository {
    <S extends Utente> S save(S utente);
    Optional<Utente> findById(String id);
    Optional<Utente> findByEmail(String email);
}
