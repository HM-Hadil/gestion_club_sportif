package esprit.gestion_club_sportif.repo;



import esprit.gestion_club_sportif.entities.Salle;
import esprit.gestion_club_sportif.entities.SalleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SalleRepository extends JpaRepository<Salle, Long> {
    List<Salle> findByStatus(SalleStatus status);
}
