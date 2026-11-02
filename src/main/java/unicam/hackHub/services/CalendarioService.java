package unicam.hackHub.services;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class CalendarioService implements ICalendario {

    private final Set<String> slotPrenotati = ConcurrentHashMap.newKeySet();

    @Override
    public void prenotaCall(String slot) {
        if (slot == null || slot.isBlank()) {
            throw new IllegalArgumentException("Lo slot è obbligatorio.");
        }

        if (!slotPrenotati.add(slot)) {
            throw new IllegalStateException("Lo slot è già occupato.");
        }
    }
}