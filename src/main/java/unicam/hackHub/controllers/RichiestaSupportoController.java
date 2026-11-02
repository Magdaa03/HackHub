package unicam.hackHub.controllers;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import unicam.hackHub.entities.hackathon.RichiestaSupporto;
import unicam.hackHub.state.StatoRichiestaSupporto;
import unicam.hackHub.services.RichiestaSupportoService;

@RestController
@RequestMapping("/api")
public class RichiestaSupportoController {

    private final RichiestaSupportoService richiestaSupportoService;

    public RichiestaSupportoController(
            RichiestaSupportoService richiestaSupportoService) {
        this.richiestaSupportoService = richiestaSupportoService;
    }

    @PostMapping(
            "/hackathon/{hackathonId}/teams/{teamId}/richieste-supporto")
    @ResponseStatus(HttpStatus.CREATED)
    public RichiestaSupportoResponse richiediSupporto(
            @PathVariable String hackathonId,
            @PathVariable String teamId,
            @RequestBody RichiestaSupportoRequest request) {

        RichiestaSupporto richiesta =
                richiestaSupportoService.creaRichiestaSupporto(
                        hackathonId,
                        request.slot(),
                        teamId,
                        request.mentoreId(),
                        request.descrizione());

        return RichiestaSupportoResponse.from(richiesta);
    }

    @PutMapping("/richieste-supporto/{richiestaId}/risposta")
    public RichiestaSupportoResponse rispondiRichiesta(
            @PathVariable String richiestaId,
            @RequestBody RispostaRichiestaRequest request) {

        RichiestaSupporto richiesta =
                richiestaSupportoService.gestisciRispostaRichiesta(
                        richiestaId, request.accetta());

        return RichiestaSupportoResponse.from(richiesta);
    }

    public record RichiestaSupportoRequest(
            String mentoreId,
            String slot,
            String descrizione) { }

    public record RispostaRichiestaRequest(boolean accetta) { }

    public record RichiestaSupportoResponse(
            String id,
            String slot,
            String descrizione,
            Date data,
            StatoRichiestaSupporto stato,
            String teamId,
            String hackathonId,
            String mentoreId) {

        private static RichiestaSupportoResponse from(
                RichiestaSupporto richiesta) {
            return new RichiestaSupportoResponse(
                    richiesta.getId(),
                    richiesta.getSlot(),
                    richiesta.getDescrizione(),
                    richiesta.getData(),
                    richiesta.getStato(),
                    richiesta.getTeam().getId(),
                    richiesta.getHackathon().getId(),
                    richiesta.getMentore().getId());
        }
    }
}