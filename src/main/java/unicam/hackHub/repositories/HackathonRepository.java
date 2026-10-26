package unicam.hackHub.repositories;

import java.util.List;
import java.util.Optional;
import unicam.hackHub.entities.hackathon.Hackathon;

public interface HackathonRepository {

    <S extends Hackathon> S save(S hackathon);

    Optional<Hackathon> findById(String id);

    List<Hackathon> findAll();
}
