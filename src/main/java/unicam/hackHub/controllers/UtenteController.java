package unicam.hackHub.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import unicam.hackHub.entities.utente.Utente;
import unicam.hackHub.services.UtenteService;
import unicam.hackHub.services.JwtTokenService;

@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    private final UtenteService utenteService;
    private final JwtTokenService jwtTokenService;

    public UtenteController(UtenteService utenteService, JwtTokenService jwtTokenService) {
        this.utenteService = utenteService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/registrazione")
    @ResponseStatus(HttpStatus.CREATED)
    public UtenteResponse registrazione(@RequestBody CredenzialiRegistrazione request) {
        Utente utente = utenteService.registraUtente(
                request.nome(), request.email(), request.password());
        return creaRispostaAutenticata(utente);
    }

    @PostMapping("/accesso")
    public UtenteResponse accesso(@RequestBody CredenzialiAccesso request) {
        Utente utente = utenteService.autentica(request.email(), request.password());
        return creaRispostaAutenticata(utente);
    }

    @GetMapping("/me")
    public ProfiloUtenteResponse utenteCorrente(@AuthenticationPrincipal UserDetails userDetails) {
        Utente utente = utenteService.trovaPerEmail(userDetails.getUsername());
        return new ProfiloUtenteResponse(utente.getId(), utente.getNome(), utente.getEmail());
    }

    private UtenteResponse creaRispostaAutenticata(Utente utente) {
        return UtenteResponse.autenticato(utente, jwtTokenService.creaToken(utente),
                jwtTokenService.getDurataSecondi());
    }

    public record CredenzialiRegistrazione(String nome, String email, String password) { }
    public record CredenzialiAccesso(String email, String password) { }
    public record UtenteResponse(
            String id, String nome, String email,
            String accessToken, String tokenType, long expiresIn) {
        private static UtenteResponse autenticato(Utente utente, String token, long expiresIn) {
            return new UtenteResponse(utente.getId(), utente.getNome(), utente.getEmail(), token, "Bearer", expiresIn);
        }
    }
    public record ProfiloUtenteResponse(String id, String nome, String email) { }
}
