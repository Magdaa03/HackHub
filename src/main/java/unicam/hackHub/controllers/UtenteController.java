package unicam.hackHub.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import unicam.hackHub.entities.Utente;
import unicam.hackHub.services.UtenteService;

@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    private final UtenteService utenteService;

    public UtenteController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @PostMapping("/registrazione")
    @ResponseStatus(HttpStatus.CREATED)
    public UtenteResponse registrazione(@RequestBody CredenzialiRegistrazione request) {
        Utente utente = utenteService.registraUtente(
                request.nome(), request.email(), request.password());
        return UtenteResponse.from(utente);
    }

    @PostMapping("/accesso")
    public UtenteResponse accesso(@RequestBody CredenzialiAccesso request) {
        Utente utente = utenteService.autentica(request.email(), request.password());
        return UtenteResponse.from(utente);
    }

    public record CredenzialiRegistrazione(String nome, String email, String password) { }
    public record CredenzialiAccesso(String email, String password) { }
    public record UtenteResponse(String id, String nome, String email) {
        private static UtenteResponse from(Utente utente) {
            return new UtenteResponse(utente.getId(), utente.getNome(), utente.getEmail());
        }
    }
}
