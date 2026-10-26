package unicam.hackHub.entities.hackathon;

import unicam.hackHub.state.IStatoHackathon;
import unicam.hackHub.state.StatoConcluso;
import unicam.hackHub.state.StatoInCorso;
import unicam.hackHub.state.StatoInValutazione;
import unicam.hackHub.state.StatoIscrizione;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Hackathon {

    @Id
    private String id;

    private String nome;
    private String regolamento;

    @Temporal(TemporalType.DATE)
    private Date scadenzaIscrizioni;

    @Temporal(TemporalType.DATE)
    private Date dataInizio;

    @Temporal(TemporalType.DATE)
    private Date dataFine;

    private String luogo;
    private double premio;
    private int maxTeamSize;

    /* JPA salva l'enum; il comportamento dello State viene ricostruito quando si legge l'entità. */
    @Enumerated(EnumType.STRING)
    private StatoHackathon statoPersistito = StatoHackathon.IN_ISCRIZIONE;

    @Transient
    private IStatoHackathon stato;

    protected Hackathon() {
        // Costruttore richiesto da JPA.
    }

    public Hackathon(String nome, String regolamento, Date scadenzaIscrizioni,
            Date dataInizio, Date dataFine, String luogo, double premio, int maxTeamSize) {
        this.id = UUID.randomUUID().toString();
        this.nome = testoObbligatorio(nome, "Il nome è obbligatorio.");
        this.regolamento = testoObbligatorio(regolamento, "Il regolamento è obbligatorio.");
        this.luogo = testoObbligatorio(luogo, "Il luogo è obbligatorio.");
        validaDate(scadenzaIscrizioni, dataInizio, dataFine);
        if (premio < 0) {
            throw new IllegalArgumentException("Il premio non può essere negativo.");
        }
        if (maxTeamSize < 1) {
            throw new IllegalArgumentException("La dimensione massima del team deve essere almeno 1.");
        }

        this.scadenzaIscrizioni = copia(scadenzaIscrizioni);
        this.dataInizio = copia(dataInizio);
        this.dataFine = copia(dataFine);
        this.premio = premio;
        this.maxTeamSize = maxTeamSize;
        this.statoPersistito = StatoHackathon.IN_ISCRIZIONE;
        this.stato = new StatoIscrizione();
    }

    public void cambiaStato(IStatoHackathon nuovoStato) {
        Objects.requireNonNull(nuovoStato, "Lo stato dell'hackathon è obbligatorio.");
        this.stato = nuovoStato;
        this.statoPersistito = nuovoStato.getTipo();
    }

    public IStatoHackathon getStato() {
        if (stato == null) {
            stato = switch (statoPersistito == null ? StatoHackathon.IN_ISCRIZIONE : statoPersistito) {
                case IN_ISCRIZIONE -> new StatoIscrizione();
                case IN_CORSO -> new StatoInCorso();
                case IN_VALUTAZIONE -> new StatoInValutazione();
                case CONCLUSO -> new StatoConcluso();
            };
        }
        return stato;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getRegolamento() { return regolamento; }
    public Date getScadenzaIscrizioni() { return copia(scadenzaIscrizioni); }
    public Date getDataInizio() { return copia(dataInizio); }
    public Date getDataFine() { return copia(dataFine); }
    public String getLuogo() { return luogo; }
    public double getPremio() { return premio; }
    public int getMaxTeamSize() { return maxTeamSize; }

    private static String testoObbligatorio(String valore, String messaggio) {
        if (valore == null || valore.isBlank()) {
            throw new IllegalArgumentException(messaggio);
        }
        return valore.trim();
    }

    private static void validaDate(Date scadenzaIscrizioni, Date dataInizio, Date dataFine) {
        Objects.requireNonNull(scadenzaIscrizioni, "La scadenza delle iscrizioni è obbligatoria.");
        Objects.requireNonNull(dataInizio, "La data di inizio è obbligatoria.");
        Objects.requireNonNull(dataFine, "La data di fine è obbligatoria.");
        if (scadenzaIscrizioni.after(dataInizio) || !dataInizio.before(dataFine)) {
            throw new IllegalArgumentException("Le date dell'hackathon non sono coerenti.");
        }
    }

    private static Date copia(Date data) {
        return data == null ? null : new Date(data.getTime());
    }
}
