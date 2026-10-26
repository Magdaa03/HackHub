package unicam.hackHub.state;

import unicam.hackHub.entities.hackathon.StatoHackathon;

/** Comportamenti dipendenti dallo stato corrente dell'hackathon. */
public interface IStatoHackathon {

    StatoHackathon getTipo();

    boolean consenteIscrizioni();

    boolean consenteInvioSottomissione();

    boolean consenteAggiornamentoSottomissione();
}
