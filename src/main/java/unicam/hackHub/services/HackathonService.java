package unicam.hackHub.services;

import java.util.Date;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import unicam.hackHub.entities.hackathon.Hackathon;
import unicam.hackHub.factory.HackathonBuilder;
import unicam.hackHub.repositories.HackathonRepository;

@Service
public class HackathonService {

    private final HackathonRepository hackathonRepository;

    public HackathonService(HackathonRepository hackathonRepository) {
        this.hackathonRepository = hackathonRepository;
    }

    @Transactional
    public Hackathon creaHackathon(String nome, String regolamento,
            Date scadenzaIscrizioni, Date dataInizio, Date dataFine,
            String luogo, double premio, int maxTeamSize) {
        Hackathon hackathon = new HackathonBuilder()
                .nome(nome)
                .regolamento(regolamento)
                .scadenzaIscrizione(scadenzaIscrizioni)
                .dataInizio(dataInizio)
                .dataFine(dataFine)
                .luogo(luogo)
                .premio(premio)
                .maxTeamSize(maxTeamSize)
                .build();

        return hackathonRepository.save(hackathon);
    }

    @Transactional(readOnly = true)
    public Hackathon trovaPerId(String id) {
        return hackathonRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hackathon non trovato."));
    }

    @Transactional(readOnly = true)
    public List<Hackathon> trovaTutti() {
        return hackathonRepository.findAll();
    }
}
