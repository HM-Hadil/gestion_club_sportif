package esprit.gestion_club_sportif.repo;


import esprit.gestion_club_sportif.entities.Inscription;
import esprit.gestion_club_sportif.entities.Seance;
import esprit.gestion_club_sportif.entities.User;
import esprit.gestion_club_sportif.response.InscriptionResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    boolean existsBySeanceAndJoueur(Seance seance, User joueur);
    List<Inscription> findByJoueurIdOrderByDateInscriptionDesc(UUID joueurId);

    List<Inscription> findBySeanceIdOrderByDateInscriptionDesc(Long seanceId); // Changed return type and added Desc
}