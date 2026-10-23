package unicam.hackHub.services;

import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import unicam.hackHub.entities.Utente;
import unicam.hackHub.repositories.UtenteRepository;

@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;

    public UtenteService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    @Transactional
    public Utente registraUtente(String nome, String email, String password) {
        if (nome == null || nome.isBlank() || email == null || email.isBlank()
                || password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nome, email e password sono obbligatori.");
        }

        String emailNormalizzata = email.trim().toLowerCase(Locale.ROOT);
        if (utenteRepository.findByEmail(emailNormalizzata).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Esiste già un account con questa email.");
        }

        Utente utente = new Utente(
                UUID.randomUUID().toString(),
                nome.trim(),
                emailNormalizzata,
                password);
        return utenteRepository.save(utente);
    }

    @Transactional(readOnly = true)
    public Utente autentica(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw credenzialiNonValide();
        }

        String emailNormalizzata = email.trim().toLowerCase(Locale.ROOT);
        Utente utente = utenteRepository.findByEmail(emailNormalizzata)
                .orElseThrow(this::credenzialiNonValide);

        if (!password.equals(utente.getPassword())) {
            throw credenzialiNonValide();
        }
        return utente;
    }

    private ResponseStatusException credenzialiNonValide() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Email o password non corretti.");
    }
}
