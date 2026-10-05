package unicam.hackHub.team.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import unicam.hackHub.utente.model.Utente;

@Entity
public class Invito {

    @Id
    private String id;

    private Date dataInvio;
    private StatoInvito stato;

    @ManyToOne
    private Team mittente;

    @ManyToOne
    private Utente destinatario;

    //costruttore per leggere db
    public Invito() {}

    //costruttore
    public Invito(String id, Date dataInvio, StatoInvito stato, Team mittente, Utente destinatario){
        this.id=id;
        this.dataInvio=dataInvio;
        this.stato=stato;
        this.mittente=mittente;
        this.destinatario=destinatario;
    }

    public void accetta(){
        this.stato=StatoInvito.ACCETTATO;
    }

    public void rifiuta(){
        this.stato=StatoInvito.RIFIUTATO;
    }

    // --- Getter e Setter ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDataInvio() {
        return dataInvio;
    }

    public void setDataInvio(Date dataInvio) {
        this.dataInvio = dataInvio;
    }

    public StatoInvito getStato() {
        return stato;
    }

    public void setStato(StatoInvito stato) {
        this.stato = stato;
    }

    public Team getMittente() {
        return mittente;
    }

    public void setMittente(Team mittente) {
        this.mittente = mittente;
    }

    public Utente getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Utente destinatario) {
        this.destinatario = destinatario;
    }
}
