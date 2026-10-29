package unicam.hackHub.controllers;

import java.util.Date;
import org.springframework.http.HttpStatus;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import unicam.hackHub.entities.hackathon.Sottomissione;
import unicam.hackHub.services.SottomissioneService;

@RestController
@RequestMapping("/api")
public class SottomissioneController {

    private final SottomissioneService sottomissioneService;

    public SottomissioneController(SottomissioneService sottomissioneService) {
        this.sottomissioneService = sottomissioneService;
    }

    @PostMapping("/hackathon/{idHackathon}/teams/{idTeam}/sottomissioni")
    @ResponseStatus(HttpStatus.CREATED)
    public SottomissioneResponse inviaSottomissione(
            @PathVariable String idHackathon,
            @PathVariable String idTeam,
            @RequestBody SottomissioneRequest request,
            @AuthenticationPrincipal UserDetails utenteAutenticato) {
        Sottomissione sottomissione = sottomissioneService.inviaSottomissione(
                idHackathon,
                idTeam,
                utenteAutenticato.getUsername(),
                request.descrizione());

        return SottomissioneResponse.from(sottomissione);
    }

    @PutMapping("/sottomissioni/{idSottomissione}")
    public SottomissioneResponse aggiornaSottomissione(
            @PathVariable String idSottomissione,
            @RequestBody SottomissioneRequest request,
            @AuthenticationPrincipal UserDetails utenteAutenticato) {
        Sottomissione sottomissione = sottomissioneService.aggiornaSottomissione(
                idSottomissione,
                utenteAutenticato.getUsername(),
                request.descrizione());

        return SottomissioneResponse.from(sottomissione);
    }

    public record SottomissioneRequest(String descrizione) { }

    public record SottomissioneResponse(
            String id,
            String descrizione,
            Date data,
            String teamId,
            String hackathonId) {

        private static SottomissioneResponse from(Sottomissione sottomissione) {
            return new SottomissioneResponse(
                    sottomissione.getId(),
                    sottomissione.getDescrizione(),
                    sottomissione.getData(),
                    sottomissione.getTeam().getId(),
                    sottomissione.getHackathon().getId());
        }
    }
}