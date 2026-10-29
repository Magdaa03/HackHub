package unicam.hackHub.repositories;

import java.util.Optional;
import unicam.hackHub.entities.hackathon.Sottomissione;

public interface SottomissioneRepository {

    <S extends Sottomissione> S save(S sottomissione);

    Optional<Sottomissione> findById(String id);

    boolean existsByTeamIdAndHackathonId(String teamId, String hackathonId);
}