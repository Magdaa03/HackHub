package unicam.hackHub.entities.staff;

import jakarta.persistence.Entity;
import unicam.hackHub.entities.utente.Utente;

@Entity
public class Mentore extends ProfiloStaff {
    protected Mentore() { }

    public Mentore(Utente utente) {
        super(utente);
    }
}
