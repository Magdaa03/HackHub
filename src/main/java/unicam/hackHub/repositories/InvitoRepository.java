package unicam.hackHub.repositories;

import java.util.List;
import java.util.Optional;
import unicam.hackHub.entities.Invito;
import unicam.hackHub.entities.StatoInvito;
import unicam.hackHub.entities.Utente;

public interface InvitoRepository {
    <S extends Invito> S save(S invito);
    Optional<Invito> findById(String id);
    List<Invito> findByDestinatarioAndStato(Utente destinatario, StatoInvito stato);
}
