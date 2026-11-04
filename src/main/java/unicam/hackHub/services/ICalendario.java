package unicam.hackHub.services;

public interface ICalendario {
    boolean slotDisponibile(String slot);
    String prenotaCall(String slot, String titolo, String descrizione);
    void annullaCall(String idEvento);
}
