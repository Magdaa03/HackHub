package unicam.hackHub.repositories;

import java.util.Optional;
import unicam.hackHub.entities.Team;

public interface TeamRepository {
    <S extends Team> S save(S team);
    Optional<Team> findById(String id);
    Optional<Team> findByName(String nome);
    boolean existsByMembri_Id(String utenteId);
}
