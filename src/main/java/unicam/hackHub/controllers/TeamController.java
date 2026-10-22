package unicam.hackHub.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import unicam.hackHub.entities.Team;
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
    public TeamResponse creaTeam(@RequestBody CreaTeamRequest request) {
        Team team = teamService.creaTeamDaRichiesta(request.nome(), request.idCreatore());
        return new TeamResponse(team.getId(), team.getNome());
    }

    public record CreaTeamRequest(String nome, String idCreatore) { }

    public record TeamResponse(String id, String nome) { }
}
