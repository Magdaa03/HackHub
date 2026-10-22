package unicam.hackHub.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import unicam.hackHub.services.TeamService;

@RestController
@RequestMapping("/api/team")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/{teamId}/inviti")
    @ResponseStatus(HttpStatus.CREATED)
    public InvitoResponse inviaInvito(
            @PathVariable String teamId,
            @RequestBody InvitaUtenteRequest request) {
        var invito = teamService.inviaInvito(teamId, request.emailDestinatario());
        return new InvitoResponse(
                invito.getId(), invito.getStato().name(), invito.getMittente().getId());
    }

    @PutMapping("/inviti/{invitoId}/risposta")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void gestisciRispostaInvito(
            @PathVariable String invitoId,
            @RequestBody RispostaInvitoRequest request) {
        teamService.gestisciRispostaInvito(invitoId, request.accetta());
    }

    @DeleteMapping("/{teamId}/membri/{utenteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void abbandonaTeam(
            @PathVariable String teamId,
            @PathVariable String utenteId) {
        teamService.abbandonaTeam(teamId, utenteId);
    }

    public record InvitaUtenteRequest(String emailDestinatario) { }
    public record RispostaInvitoRequest(boolean accetta) { }

    public record InvitoResponse(String id, String stato, String teamId) { }
}
