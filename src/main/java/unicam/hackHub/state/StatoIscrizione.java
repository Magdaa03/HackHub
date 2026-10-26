package unicam.hackHub.state;

import unicam.hackHub.entities.hackathon.StatoHackathon;

public class StatoIscrizione implements IStatoHackathon {

    @Override
    public StatoHackathon getTipo() {
        return StatoHackathon.IN_ISCRIZIONE;
    }

    @Override
    public boolean consenteIscrizioni() {
        return true;
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
