package esprit.gestion_club_sportif.repo;

import esprit.gestion_club_sportif.entities.Activite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActiviteRepository extends JpaRepository<Activite,Long> {
    List<Activite> findByEntraineurId(UUID entraineurId);


}
