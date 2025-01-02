// SalleRepository.java
package esprit.gestion_club_sportif.repo;

import esprit.gestion_club_sportif.entities.Salle;
import esprit.gestion_club_sportif.entities.SalleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalleRepository extends JpaRepository<Salle, Long> {
    List<Salle> findByStatus(SalleStatus status);
    Optional<Salle> findByNom(String nom);
}