package unicam.hackHub.state;

import unicam.hackHub.entities.hackathon.StatoHackathon;

public class StatoConcluso implements IStatoHackathon {

    @Override
    public StatoHackathon getTipo() {
        return StatoHackathon.CONCLUSO;
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
