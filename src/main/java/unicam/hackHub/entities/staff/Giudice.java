package unicam.hackHub.entities.staff;

import jakarta.persistence.Entity;
import unicam.hackHub.entities.utente.Utente;

@Entity
public class Giudice extends ProfiloStaff {
    protected Giudice() { }

    public Giudice(Utente utente) {
        super(utente);
    }
}
