package unicam.hackHub.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import unicam.hackHub.entities.staff.ProfiloStaff;

public interface ProfiloStaffRepository extends JpaRepository<ProfiloStaff, String> {
}
