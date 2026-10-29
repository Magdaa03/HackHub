package unicam.hackHub.entities.hackathon;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import unicam.hackHub.entities.team.Team;

@Entity
public class Iscrizione {

    @Id
    private String id;

    @Temporal(TemporalType.DATE)
    private Date dataIscrizione;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathon hackathon;

    protected Iscrizione() { }

    public Iscrizione(Team team, Hackathon hackathon) {
        this.id = UUID.randomUUID().toString();
        this.dataIscrizione = new Date();
        this.team = Objects.requireNonNull(team);
        this.hackathon = Objects.requireNonNull(hackathon);
    }

    public String getId() {
        return id;
    }

    public Date getDataIscrizione() {
        return new Date(dataIscrizione.getTime());
    }

    public Team getTeam() {
        return team;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }
}
