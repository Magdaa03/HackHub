package unicam.hackHub.repositories;

import java.util.Optional;
import unicam.hackHub.entities.hackathon.RichiestaSupporto;

public interface RichiestaSupportoRepository {

    <S extends RichiestaSupporto> S save(S richiesta);

    Optional<RichiestaSupporto> findById(String id);
}