package unicam.hackHub.entities.hackathon;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;
import unicam.hackHub.entities.staff.Mentore;
import unicam.hackHub.entities.team.Team;
import unicam.hackHub.state.StatoRichiestaSupporto;

@Entity
public class RichiestaSupporto {

    @Id
    private String id;

    private String slot;
    private String descrizione;

    /** Id dell'evento sul calendario esterno, necessario per annullare la call. */
    private String idEventoCalendario;

    @Temporal(TemporalType.TIMESTAMP)
    private Date data;

    @Enumerated(EnumType.STRING)
    private StatoRichiestaSupporto stato;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathon hackathon;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mentore_id", nullable = false)
    private Mentore mentore;

    protected RichiestaSupporto() { }

    public RichiestaSupporto(
            String slot,
            String descrizione,
            String idEventoCalendario,
            Team team,
            Hackathon hackathon,
            Mentore mentore) {
        this.id = UUID.randomUUID().toString();
        this.slot = slot;
        this.descrizione = descrizione;
        this.idEventoCalendario = idEventoCalendario;
        this.data = new Date();
        this.stato = StatoRichiestaSupporto.PENDENTE;
        this.team = team;
        this.hackathon = hackathon;
        this.mentore = mentore;
    }

    public void accettaCall(String slot) {
        verificaPendente();
        if (slot == null || slot.isBlank()) {
            throw new IllegalArgumentException("Lo slot è obbligatorio.");
        }
        this.slot = slot;
        this.stato = StatoRichiestaSupporto.PROGRAMMATA;
    }

    public void rifiutaCall(String slot) {
        verificaPendente();
        this.slot = slot;
        this.stato = StatoRichiestaSupporto.RIFIUTATA;
        this.idEventoCalendario = null;
    }

    private void verificaPendente() {
        if (stato != StatoRichiestaSupporto.PENDENTE) {
            throw new IllegalStateException("La richiesta è già stata gestita.");
        }
    }

    public String getId() { return id; }
    public String getSlot() { return slot; }
    public String getDescrizione() { return descrizione; }
    public String getIdEventoCalendario() { return idEventoCalendario; }
    public Date getData() { return data == null ? null : new Date(data.getTime()); }
    public StatoRichiestaSupporto getStato() { return stato; }
    public Team getTeam() { return team; }
    public Hackathon getHackathon() { return hackathon; }
    public Mentore getMentore() { return mentore; }
}