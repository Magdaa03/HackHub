package unicam.hackHub.controllers;

import java.util.Date;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponse creaTeam(
            @RequestBody CreaTeamRequest request,
            @AuthenticationPrincipal UserDetails utenteAutenticato) {
        var team = teamService.creaTeam(request.nome(), utenteAutenticato.getUsername());
        return new TeamResponse(team.getId(), team.getNome());
    }

    @PostMapping("/{teamId}/inviti")
    @ResponseStatus(HttpStatus.CREATED)
    public InvitoResponse inviaInvito(
            @PathVariable String teamId,
            @RequestBody InvitaUtenteRequest request,
            @AuthenticationPrincipal UserDetails utenteAutenticato) {
        var invito = teamService.inviaInvito(
                teamId, utenteAutenticato.getUsername(), request.emailDestinatario());
        return new InvitoResponse(
                invito.getId(), invito.getStato().name(), invito.getMittente().getId());
    }

    @GetMapping("/inviti")
    public List<InvitoRicevutoResponse> listaInvitiRicevuti(
            @AuthenticationPrincipal UserDetails utenteAutenticato) {
        return teamService.listaInvitiRicevuti(utenteAutenticato.getUsername()).stream()
                .map(invito -> new InvitoRicevutoResponse(
                        invito.getId(),
                        invito.getMittente().getId(),
                        invito.getMittente().getNome(),
                        invito.getDataInvio(),
                        invito.getStato().name()))
                .toList();
    }

    @PutMapping("/inviti/{invitoId}/risposta")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void gestisciRispostaInvito(
            @PathVariable String invitoId,
            @RequestBody RispostaInvitoRequest request,
            @AuthenticationPrincipal UserDetails utenteAutenticato) {
        teamService.gestisciRispostaInvito(
                invitoId, utenteAutenticato.getUsername(), request.accetta());
    }

    @DeleteMapping("/{teamId}/abbandona")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void abbandonaTeam(
            @PathVariable String teamId,
            @AuthenticationPrincipal UserDetails utenteAutenticato) {
        teamService.abbandonaTeam(teamId, utenteAutenticato.getUsername());
    }

    public record InvitaUtenteRequest(String emailDestinatario) { }
    public record RispostaInvitoRequest(boolean accetta) { }
    public record CreaTeamRequest(String nome) { }

    public record TeamResponse(String id, String nome) { }
    public record InvitoResponse(String id, String stato, String teamId) { }
    public record InvitoRicevutoResponse(
            String id, String teamId, String nomeTeam, Date dataInvio, String stato) { }
}
