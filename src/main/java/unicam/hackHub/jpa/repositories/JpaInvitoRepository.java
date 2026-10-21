package unicam.hackHub.jpa.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicam.hackHub.entities.Invito;
import unicam.hackHub.entities.StatoInvito;
import unicam.hackHub.entities.Utente;
import unicam.hackHub.repositories.InvitoRepository;

@Repository
public interface JpaInvitoRepository
        extends JpaRepository<Invito, String>, InvitoRepository {

    @Override
    default Invito saveInvito(Invito invito) {
        return save(invito);
    }

    @Override
    List<Invito> findByDestinatarioAndStato(Utente destinatario, StatoInvito stato);
}