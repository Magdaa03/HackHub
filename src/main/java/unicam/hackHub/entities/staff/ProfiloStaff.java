package unicam.hackHub.entities.staff;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import unicam.hackHub.entities.utente.Utente;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "utente_id"))
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ProfiloStaff implements MembroStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(optional = false)
    @JoinColumn(name = "utente_id", nullable = false, unique = true)
    private Utente utente;

    protected ProfiloStaff() { }

    protected ProfiloStaff(Utente utente) {
        this.utente = utente;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNome() {
        return utente.getNome();
    }

    @Override
    public String getEmail() {
        return utente.getEmail();
    }

    public Utente getUtente() {
        return utente;
    }
}
