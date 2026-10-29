package unicam.hackHub.services;

import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import unicam.hackHub.entities.staff.Giudice;
import unicam.hackHub.entities.staff.Mentore;
import unicam.hackHub.entities.staff.Organizzatore;
import unicam.hackHub.entities.staff.ProfiloStaff;
import unicam.hackHub.entities.staff.TipoProfiloStaff;
import unicam.hackHub.entities.utente.RuoloAccount;
import unicam.hackHub.entities.utente.Utente;
import unicam.hackHub.repositories.ProfiloStaffRepository;
import unicam.hackHub.repositories.UtenteRepository;

@Service
public class GestoreService {

    private final UtenteRepository utenteRepository;
    private final ProfiloStaffRepository profiloStaffRepository;
    private final PasswordEncoder passwordEncoder;

    public GestoreService(
            UtenteRepository utenteRepository,
            ProfiloStaffRepository profiloStaffRepository,
            PasswordEncoder passwordEncoder) {
        this.utenteRepository = utenteRepository;
        this.profiloStaffRepository = profiloStaffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ProfiloStaff creaAccountStaff(
            String nome, String email, String password, TipoProfiloStaff tipoProfilo) {
        if (nome == null || nome.isBlank() || email == null || email.isBlank()
                || password == null || password.isBlank() || tipoProfilo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nome, email, password e tipo di profilo sono obbligatori.");
        }

        String emailNormalizzata = email.trim().toLowerCase(Locale.ROOT);
        if (utenteRepository.findByEmail(emailNormalizzata).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Esiste già un account con questa email.");
        }

        Utente account = new Utente(UUID.randomUUID().toString(), nome.trim(),
                emailNormalizzata, passwordEncoder.encode(password));
        account.setRuolo(RuoloAccount.STAFF);
        Utente accountSalvato = utenteRepository.save(account);

        ProfiloStaff profilo = switch (tipoProfilo) {
            case ORGANIZZATORE -> new Organizzatore(accountSalvato);
            case GIUDICE -> new Giudice(accountSalvato);
            case MENTORE -> new Mentore(accountSalvato);
        };
        return profiloStaffRepository.save(profilo);
    }
}
