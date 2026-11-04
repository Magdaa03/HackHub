package unicam.hackHub.services;


import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/** Implementazione di {@link ICalendario} basata su Google Calendar. */
@Service
@ConditionalOnProperty(prefix = "hackhub.calendar", name = "enabled", havingValue = "true")
public class CalendarioService implements ICalendario {

    private final Calendar googleCalendar;
    private final String calendarId;
    private final int durataMinuti;

    public CalendarioService(
            Calendar googleCalendar,
            @Value("${hackhub.calendar.id}") String calendarId,
            @Value("${hackhub.calendar.durata-minuti:30}") int durataMinuti) {
        this.googleCalendar = googleCalendar;
        this.calendarId = calendarId;
        this.durataMinuti = durataMinuti;
    }

    @Override
    public boolean slotDisponibile(String slot) {
        Intervallo intervallo = interpreta(slot);
        try {
            FreeBusyRequest request = new FreeBusyRequest()
                    .setTimeMin(new DateTime(intervallo.inizio().toInstant().toEpochMilli()))
                    .setTimeMax(new DateTime(intervallo.fine().toInstant().toEpochMilli()))
                    .setItems(List.of(new FreeBusyRequestItem().setId(calendarId)));
            FreeBusyResponse response = googleCalendar.freebusy().query(request).execute();
            var calendario = response.getCalendars().get(calendarId);
            return calendario != null
                    && (calendario.getBusy() == null || calendario.getBusy().isEmpty());
        } catch (IOException e) {
            throw new IllegalStateException("Non è stato possibile verificare Google Calendar.", e);
        }
    }

    @Override
    public String prenotaCall(String slot, String titolo, String descrizione) {
        Intervallo intervallo = interpreta(slot);
        if (!slotDisponibile(slot)) {
            throw new IllegalStateException("Lo slot selezionato è già occupato.");
        }

        Event evento = new Event()
                .setSummary(titolo)
                .setDescription(descrizione)
                .setStart(new EventDateTime()
                        .setDateTime(new DateTime(intervallo.inizio().toInstant().toEpochMilli())))
                .setEnd(new EventDateTime()
                        .setDateTime(new DateTime(intervallo.fine().toInstant().toEpochMilli())));
        try {
            return googleCalendar.events().insert(calendarId, evento)
                    .setSendUpdates("none")
                    .execute()
                    .getId();
        } catch (IOException e) {
            throw new IllegalStateException("Non è stato possibile prenotare su Google Calendar.", e);
        }
    }

    @Override
    public void annullaCall(String idEvento) {
        if (idEvento == null || idEvento.isBlank()) {
            return;
        }
        try {
            googleCalendar.events().delete(calendarId, idEvento).execute();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Non è stato possibile annullare l'evento su Google Calendar.", e);
        }
    }

    private Intervallo interpreta(String slot) {
        if (slot == null || slot.isBlank()) {
            throw new IllegalArgumentException(
                    "Lo slot deve essere una data e ora ISO 8601 con fuso orario.");
        }
        OffsetDateTime inizio;
        try {
            inizio = OffsetDateTime.parse(slot);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Formato slot non valido. Usa, per esempio, 2026-11-03T10:00:00+01:00.", e);
        }
        if (!inizio.isAfter(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Lo slot deve essere nel futuro.");
        }
        return new Intervallo(inizio, inizio.plusMinutes(durataMinuti));
    }

    private record Intervallo(OffsetDateTime inizio, OffsetDateTime fine) { }
}