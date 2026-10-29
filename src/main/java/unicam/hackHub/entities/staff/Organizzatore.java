package unicam.hackHub.entities.staff;

import jakarta.persistence.Entity;
import unicam.hackHub.entities.utente.Utente;

@Entity
public class Organizzatore extends ProfiloStaff {
    protected Organizzatore() { }

    public Organizzatore(Utente utente) {
        super(utente);
    }
}
