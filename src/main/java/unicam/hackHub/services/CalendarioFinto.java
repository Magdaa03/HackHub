package unicam.hackHub.services;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Implementazione di {@link ICalendario} che tiene le prenotazioni in memoria.
 * È attiva quando {@code hackhub.calendar.enabled} è false o assente,
 * così l'applicazione parte anche senza Google Calendar (sviluppo, test, demo).
 * Le prenotazioni si perdono al riavvio.
 */
@Service
@ConditionalOnProperty(
        prefix = "hackhub.calendar", name = "enabled", havingValue = "false", matchIfMissing = true)
public class CalendarioFinto implements ICalendario {

    /** slot -> id evento */
    private final Map<String, String> prenotazioni = new ConcurrentHashMap<>();

    @Override
    public boolean slotDisponibile(String slot) {
        verificaSlot(slot);
        return !prenotazioni.containsKey(slot.trim());
    }

    @Override
    public String prenotaCall(String slot, String titolo, String descrizione) {
        verificaSlot(slot);
        String idEvento = UUID.randomUUID().toString();
        if (prenotazioni.putIfAbsent(slot.trim(), idEvento) != null) {
            throw new IllegalStateException("Lo slot selezionato è già occupato.");
        }
        return idEvento;
    }

    @Override
    public void annullaCall(String idEvento) {
        if (idEvento == null || idEvento.isBlank()) {
            return;
        }
        prenotazioni.values().remove(idEvento);
    }

    private static void verificaSlot(String slot) {
        if (slot == null || slot.isBlank()) {
            throw new IllegalArgumentException("Lo slot è obbligatorio.");
        }
    }
}
