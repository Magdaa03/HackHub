package unicam.hackHub.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import unicam.hackHub.services.IscrizioneService;

@RestController
@RequestMapping("/api/hackathon")
public class IscrizioneController {

    private final IscrizioneService iscrizioneService;

    public IscrizioneController(IscrizioneService iscrizioneService) {
        this.iscrizioneService = iscrizioneService;
    }

    @PostMapping("/{idHackathon}/teams/{idTeam}/iscrizioni")
    @ResponseStatus(HttpStatus.CREATED)
    public void iscriviTeam(
            @PathVariable String idHackathon,
            @PathVariable String idTeam,
            @AuthenticationPrincipal UserDetails utenteAutenticato) {
        iscrizioneService.iscriviTeam(
                idHackathon, utenteAutenticato.getUsername(), idTeam);
    }
}