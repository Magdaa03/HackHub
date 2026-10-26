package unicam.hackHub.repositories;

import java.util.List;
import java.util.Optional;
import unicam.hackHub.entities.team.Invito;
import unicam.hackHub.entities.team.StatoInvito;
import unicam.hackHub.entities.utente.Utente;

public interface InvitoRepository {
    <S extends Invito> S save(S invito);
    Optional<Invito> findById(String id);
    List<Invito> findByDestinatarioAndStato(Utente destinatario, StatoInvito stato);
}
