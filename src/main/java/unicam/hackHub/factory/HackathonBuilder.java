package unicam.hackHub.factory;

import java.util.Date;
import unicam.hackHub.entities.hackathon.Hackathon;

public class HackathonBuilder {

    private String nome;
    private String regolamento;
    private Date scadenzaIscrizioni;
    private Date dataInizio;
    private Date dataFine;
    private String luogo;
    private double premio;
    private int maxTeamSize;

    public HackathonBuilder nome(String nome) {
        this.nome = nome;
        return this;
    }

    public HackathonBuilder regolamento(String regolamento) {
        this.regolamento = regolamento;
        return this;
    }

    public HackathonBuilder scadenzaIscrizione(Date scadenzaIscrizioni) {
        this.scadenzaIscrizioni = scadenzaIscrizioni;
        return this;
    }

    public HackathonBuilder dataInizio(Date dataInizio) {
        this.dataInizio = dataInizio;
        return this;
    }

    public HackathonBuilder dataFine(Date dataFine) {
        this.dataFine = dataFine;
        return this;
    }

    public HackathonBuilder luogo(String luogo) {
        this.luogo = luogo;
        return this;
    }

    public HackathonBuilder premio(double premio) {
        this.premio = premio;
        return this;
    }

    public HackathonBuilder maxTeamSize(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
        return this;
    }

    public Hackathon build() {
        return new Hackathon(nome, regolamento, scadenzaIscrizioni,
                dataInizio, dataFine, luogo, premio, maxTeamSize);
    }
}
