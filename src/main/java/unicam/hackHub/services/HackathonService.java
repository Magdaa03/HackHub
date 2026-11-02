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
import java.time.LocalDate;
import java.time.ZoneId;
import unicam.hackHub.entities.hackathon.StatoHackathon;
import unicam.hackHub.state.StatoInCorso;
import unicam.hackHub.entities.staff.ProfiloStaff;
import unicam.hackHub.entities.staff.Giudice;
import unicam.hackHub.entities.staff.Mentore;
import unicam.hackHub.repositories.ProfiloStaffRepository;
import java.util.ArrayList;

@Service
public class HackathonService {

    private final HackathonRepository hackathonRepository;
    private final ProfiloStaffRepository profiloStaffRepository;

    public HackathonService(
            HackathonRepository hackathonRepository,
            ProfiloStaffRepository profiloStaffRepository) {
        this.hackathonRepository = hackathonRepository;
        this.profiloStaffRepository = profiloStaffRepository;
    }

    @Transactional
    public Hackathon creaHackathon(
            String giudiceId,
            List<String> mentoriIds,
            String nome,
            String regolamento,
            Date scadenzaIscrizioni,
            Date dataInizio,
            Date dataFine,
            String luogo,
            double premio,
            int maxTeamSize) {

        if (mentoriIds == null || mentoriIds.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "È necessario assegnare almeno un mentore.");
        }

        ProfiloStaff profiloGiudice = profiloStaffRepository.findById(giudiceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Giudice non trovato."));

        if (!(profiloGiudice instanceof Giudice giudice)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Il profilo selezionato non è un giudice.");
        }

        List<Mentore> mentori = new ArrayList<>();
        for (String mentoreId : mentoriIds) {
            ProfiloStaff profilo = profiloStaffRepository.findById(mentoreId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Mentore non trovato: " + mentoreId));

            if (!(profilo instanceof Mentore mentore)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Il profilo selezionato non è un mentore: " + mentoreId);
            }

            mentori.add(mentore);
        }

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

        hackathon.assegnaGiudice(giudice);

        for (Mentore mentore : mentori) {
            hackathon.aggiungiMentore(mentore);
        }

        return hackathonRepository.save(hackathon);
    }

    @Transactional
    public Hackathon avviaHackathon(String idHackathon) {
        Hackathon hackathon = hackathonRepository.findById(idHackathon)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hackathon non trovato."));

        if (hackathon.getStato().getTipo() != StatoHackathon.IN_ISCRIZIONE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "L'hackathon non è in stato di iscrizione.");
        }

        LocalDate oggi = LocalDate.now();
        LocalDate inizio = hackathon.getDataInizio().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate fine = hackathon.getDataFine().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

        if (oggi.isBefore(inizio) || oggi.isAfter(fine)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "L'hackathon può essere avviato solo tra la data di inizio e quella di fine.");
        }

        hackathon.cambiaStato(new StatoInCorso());
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
