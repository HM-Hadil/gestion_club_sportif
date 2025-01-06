package esprit.gestion_club_sportif.repo;

import esprit.gestion_club_sportif.entities.Salle;
import esprit.gestion_club_sportif.entities.Seance;
import esprit.gestion_club_sportif.entities.StatusSeance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SeanceRepository extends JpaRepository<Seance,Long> {
    public List<Seance> findBySalleId(Long salleId) ;
    List<Seance> findByStatus(StatusSeance status);
    List<Seance> findBySalleAndDateDebutBetween(Salle salle, LocalDateTime debut, LocalDateTime fin);
    List<Seance> findBySalle(Salle salle);



    }



