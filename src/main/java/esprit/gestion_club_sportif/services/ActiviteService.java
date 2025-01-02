package esprit.gestion_club_sportif.services;


import esprit.gestion_club_sportif.entities.Activite;
import esprit.gestion_club_sportif.entities.Seance;
import esprit.gestion_club_sportif.entities.Salle;
import esprit.gestion_club_sportif.repo.ActiviteRepository;
import esprit.gestion_club_sportif.repo.SalleRepository;
import esprit.gestion_club_sportif.repo.SeanceRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActiviteService {
    private final ActiviteRepository activiteRepository;
    private final SeanceRepository seanceRepository;
    private final SalleRepository salleRepository;

    public ActiviteService(ActiviteRepository activiteRepository, SeanceRepository seanceRepository, SalleRepository salleRepository) {
        this.activiteRepository = activiteRepository;
        this.seanceRepository = seanceRepository;
        this.salleRepository = salleRepository;
    }

    public Activite createActiviteWithSeances(String nomActivite, List<Seance> seancesData, Long salleId) {
        // Créer l'activité
        Activite activite = new Activite();
        activite.setNom(nomActivite);

        // Récupérer la salle par son ID
        Salle salle = salleRepository.findById(salleId).orElseThrow(() -> new RuntimeException("Salle non trouvée"));

        // Créer et associer les séances à l'activité et à la salle
        for (Seance seanceData : seancesData) {
            Seance seance = new Seance();
            seance.setDate(seanceData.getDate());
            seance.setDuree(seanceData.getDuree());
            seance.setNombreLimite(seanceData.getNombreLimite());
            seance.setPrix(seanceData.getPrix());
            seance.setStatus(seanceData.getStatus());
            seance.setSalle(salle);
            seance.setActivite(activite);

            // Ajouter chaque séance à la liste de séances de l'activité
            activite.getSeances().add(seance);
        }

        // Sauvegarder l'activité (les séances sont automatiquement sauvegardées en raison de la relation)
        return activiteRepository.save(activite);
    }
}
