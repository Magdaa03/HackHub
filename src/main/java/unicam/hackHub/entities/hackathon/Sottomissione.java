package unicam.hackHub.entities.hackathon;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;
import unicam.hackHub.entities.team.Team;

@Entity
public class Sottomissione {

    @Id
    private String id;

    private String descrizione;

    @Temporal(TemporalType.TIMESTAMP)
    private Date data;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathon hackathon;

    protected Sottomissione() { }

    public Sottomissione(String descrizione, Team team, Hackathon hackathon) {
        this.id = UUID.randomUUID().toString();
        this.team = team;
        this.hackathon = hackathon;
        aggiorna(descrizione);
    }

    public void aggiorna(String descrizione) {
        if (descrizione == null || descrizione.isBlank()) {
            throw new IllegalArgumentException("La descrizione è obbligatoria.");
        }
        this.descrizione = descrizione.trim();
        this.data = new Date();
    }

    public String getId() { return id; }
    public String getDescrizione() { return descrizione; }
    public Date getData() { return data == null ? null : new Date(data.getTime()); }
    public Team getTeam() { return team; }
    public Hackathon getHackathon() { return hackathon; }
}