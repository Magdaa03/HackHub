package unicam.hackHub.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import unicam.hackHub.entities.staff.ProfiloStaff;
import unicam.hackHub.entities.staff.TipoProfiloStaff;
import unicam.hackHub.services.GestoreService;

@RestController
@RequestMapping("/api/gestore")
public class GestoreController {

    private final GestoreService gestoreService;

    public GestoreController(GestoreService gestoreService) {
        this.gestoreService = gestoreService;
    }

    @PostMapping("/account-staff")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountStaffResponse creaAccountStaff(@Valid @RequestBody CreaAccountStaffRequest request) {
        ProfiloStaff profilo = gestoreService.creaAccountStaff(
                request.nome(), request.email(), request.password(), request.tipoProfilo());
        return new AccountStaffResponse(
                profilo.getId(), profilo.getNome(), profilo.getEmail(), request.tipoProfilo());
    }

    public record CreaAccountStaffRequest(
            @NotBlank @Size(max = 100) String nome,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 100) String password,
            @NotNull TipoProfiloStaff tipoProfilo) { }

    public record AccountStaffResponse(
            String id, String nome, String email, TipoProfiloStaff tipoProfilo) { }
}
