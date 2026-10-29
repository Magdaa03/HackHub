package unicam.hackHub.config;

import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import unicam.hackHub.entities.utente.RuoloAccount;
import unicam.hackHub.entities.utente.Utente;
import unicam.hackHub.repositories.UtenteRepository;

@Component
public class GestoreIniziale implements CommandLineRunner {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final String nome;
    private final String email;
    private final String password;

    public GestoreIniziale(
            UtenteRepository utenteRepository,
            PasswordEncoder passwordEncoder,
            @Value("${hackhub.gestore.nome}") String nome,
            @Value("${hackhub.gestore.email}") String email,
            @Value("${hackhub.gestore.password}") String password) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
        this.nome = nome;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        String emailNormalizzata = email.trim().toLowerCase(Locale.ROOT);

        if (utenteRepository.findByEmail(emailNormalizzata).isPresent()) {
            return;
        }

        Utente gestore = new Utente(
                UUID.randomUUID().toString(),
                nome.trim(),
                emailNormalizzata,
                passwordEncoder.encode(password));

        gestore.setRuolo(RuoloAccount.GESTORE);
        utenteRepository.save(gestore);
    }
}