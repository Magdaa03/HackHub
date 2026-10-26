package unicam.hackHub.state;

import unicam.hackHub.entities.hackathon.StatoHackathon;

public class StatoInCorso implements IStatoHackathon {

    @Override
    public StatoHackathon getTipo() {
        return StatoHackathon.IN_CORSO;
    }

    @Override
    public boolean consenteIscrizioni() {
        return false;
    }

    @Override
    public boolean consenteInvioSottomissione() {
        return true;
    }

    @Override
    public boolean consenteAggiornamentoSottomissione() {
        return true;
    }
}
