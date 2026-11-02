package unicam.hackHub.entities.staff;

import jakarta.persistence.Entity;
import unicam.hackHub.entities.utente.Utente;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import unicam.hackHub.entities.hackathon.Hackathon;

@Entity
public class Mentore extends ProfiloStaff {
    protected Mentore() { }

    public Mentore(Utente utente) {
        super(utente);
    }

    @ManyToOne
    @JoinColumn(name = "hackathon_id")
    private Hackathon hackathon;

    public Hackathon getHackathon() {
        return hackathon;
    }

    public void assegnaAHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }
}
