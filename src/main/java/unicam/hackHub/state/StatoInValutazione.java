package unicam.hackHub.state;

import unicam.hackHub.entities.hackathon.StatoHackathon;

public class StatoInValutazione implements IStatoHackathon {

    @Override
    public StatoHackathon getTipo() {
        return StatoHackathon.IN_VALUTAZIONE;
    }

    @Override
    public boolean consenteIscrizioni() {
        return false;
    }

    @Override
    public boolean consenteInvioSottomissione() {
        return false;
    }

    @Override
    public boolean consenteAggiornamentoSottomissione() {
        return false;
    }
}
