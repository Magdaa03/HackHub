package unicam.hackHub.controllers;

import java.util.Date;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unicam.hackHub.entities.hackathon.Hackathon;
import unicam.hackHub.services.HackathonService;

@RestController
@RequestMapping("/api/hackathon")
public class HackathonController {

    private final HackathonService hackathonService;

    public HackathonController(HackathonService hackathonService) {
        this.hackathonService = hackathonService;
    }

    @GetMapping
    public List<HackathonResponse> visualizzaHackathon() {
        return hackathonService.trovaTutti().stream()
                .map(HackathonResponse::from)
                .toList();
    }

    @GetMapping("/{hackathonId}")
    public HackathonResponse visualizzaDettagli(@PathVariable String hackathonId) {
        return HackathonResponse.from(hackathonService.trovaPerId(hackathonId));
    }

    public record HackathonResponse(
            String id,
            String nome,
            String regolamento,
            Date scadenzaIscrizioni,
            Date dataInizio,
            Date dataFine,
            String luogo,
            double premio,
            int maxTeamSize,
            String stato) {

        private static HackathonResponse from(Hackathon hackathon) {
            return new HackathonResponse(
                    hackathon.getId(),
                    hackathon.getNome(),
                    hackathon.getRegolamento(),
                    hackathon.getScadenzaIscrizioni(),
                    hackathon.getDataInizio(),
                    hackathon.getDataFine(),
                    hackathon.getLuogo(),
                    hackathon.getPremio(),
                    hackathon.getMaxTeamSize(),
                    hackathon.getStato().getTipo().name());
        }

    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HackathonResponse creaHackathon(@RequestBody CreaHackathonRequest request) {
        var hackathon = hackathonService.creaHackathon(
                request.nome(),
                request.regolamento(),
                request.scadenzaIscrizioni(),
                request.dataInizio(),
                request.dataFine(),
                request.luogo(),
                request.premio(),
                request.maxTeamSize());

        return HackathonResponse.from(hackathon);
    }

    @PutMapping("/{hackathonId}/avvio")
    public HackathonResponse avviaHackathon(@PathVariable String hackathonId) {
        return HackathonResponse.from(
                hackathonService.avviaHackathon(hackathonId));
    }

    public record CreaHackathonRequest(
            String nome,
            String regolamento,
            Date dataInizio,
            Date dataFine,
            Date scadenzaIscrizioni,
            String luogo,
            double premio,
            int maxTeamSize) { }
}
