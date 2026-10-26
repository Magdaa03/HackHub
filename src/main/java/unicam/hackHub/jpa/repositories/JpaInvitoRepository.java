package unicam.hackHub.jpa.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicam.hackHub.entities.team.Invito;
import unicam.hackHub.entities.team.StatoInvito;
import unicam.hackHub.entities.utente.Utente;
import unicam.hackHub.repositories.InvitoRepository;

@Repository
public interface JpaInvitoRepository extends JpaRepository<Invito, String>, InvitoRepository {

    @Override
    List<Invito> findByDestinatarioAndStato(Utente destinatario, StatoInvito stato);
}
