package esprit.gestion_club_sportif.repo;

import esprit.gestion_club_sportif.entities.Seance;
import esprit.gestion_club_sportif.entities.StatusSeance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeanceRepository extends JpaRepository<Seance,Long> {
    public List<Seance> findBySalleId(Long salleId) ;
    List<Seance> findByStatus(StatusSeance status);


}
