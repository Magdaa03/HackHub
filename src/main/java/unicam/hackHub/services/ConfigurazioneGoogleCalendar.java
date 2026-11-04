package unicam.hackHub.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Crea il client Google Calendar solo se il calendario è abilitato.
 * Il percorso del file con le credenziali dell'account di servizio arriva
 * da configurazione (variabile d'ambiente), mai dal repository.
 */
@Configuration
@ConditionalOnProperty(prefix = "hackhub.calendar", name = "enabled", havingValue = "true")
class ConfigurazioneGoogleCalendar {

    @Bean
    Calendar googleCalendar(
            @Value("${hackhub.calendar.credenziali}") String percorsoCredenziali)
            throws IOException, GeneralSecurityException {
        if (percorsoCredenziali == null || percorsoCredenziali.isBlank()) {
            throw new IllegalStateException(
                    "Google Calendar è abilitato ma manca il percorso delle credenziali "
                            + "(variabile GOOGLE_APPLICATION_CREDENTIALS).");
        }

        GoogleCredentials credenziali;
        try (var input = Files.newInputStream(Path.of(percorsoCredenziali))) {
            credenziali = ServiceAccountCredentials.fromStream(input).createScoped(List.of(
                    "https://www.googleapis.com/auth/calendar.events",
                    "https://www.googleapis.com/auth/calendar.events.freebusy"));
        }

        NetHttpTransport trasporto = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(
                trasporto, GsonFactory.getDefaultInstance(), new HttpCredentialsAdapter(credenziali))
                .setApplicationName("HackHub")
                .build();
    }
}