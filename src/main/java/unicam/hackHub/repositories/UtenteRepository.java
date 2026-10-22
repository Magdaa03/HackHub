package unicam.hackHub.repositories;

import java.util.Optional;
import unicam.hackHub.entities.Utente;

public interface UtenteRepository {
    Utente saveUtente(Utente utente);
    Optional<Utente> findById(String id);
    Optional<Utente> findByEmail(String email);
}