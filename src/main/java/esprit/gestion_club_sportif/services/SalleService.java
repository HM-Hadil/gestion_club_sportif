package esprit.gestion_club_sportif.services;



import esprit.gestion_club_sportif.entities.Salle;
import esprit.gestion_club_sportif.entities.SalleStatus;
import esprit.gestion_club_sportif.entities.Seance;
import esprit.gestion_club_sportif.repo.SalleRepository;
import esprit.gestion_club_sportif.repo.SeanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalleService {
    private final SalleRepository salleRepository;
    private final SeanceRepository seanceRepository;


    public SalleService(SalleRepository salleRepository,SeanceRepository seanceRepository) {
        this.salleRepository = salleRepository;
        this.seanceRepository=seanceRepository;
    }
    // Récupérer toutes les salles
    public List<Salle> getAllSalles() {
        return salleRepository.findAll();
    }
    public List<Salle> getAvailableSalles() {
        return salleRepository.findByStatus(SalleStatus.DISPONIBLE);
    }

    public Salle updateSalleStatus(Long salleId, SalleStatus newStatus) {
        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new RuntimeException("Salle not found"));
        salle.setStatus(newStatus);
        return salleRepository.save(salle);
    }
    public boolean isSalleAvailable(Long salleId, LocalDateTime start, int duration) {
        // Récupérer toutes les séances liées à la salle pour vérifier les conflits
        List<Seance> seances = seanceRepository.findBySalleId(salleId);
        LocalDateTime end = start.plusMinutes(duration);

        return seances.stream().noneMatch(seance ->
                (seance.getDate().isBefore(end) && seance.getDate().plusMinutes(seance.getDuree()).isAfter(start))
        );
    }
    // Créer une nouvelle salle
    public Salle createSalle(Salle salle) {
        if (salleRepository.findById(salle.getId()).isPresent()) {
            throw new RuntimeException("Une salle avec ce nom existe déjà.");
        }
        return salleRepository.save(salle);
    }

    // Mettre à jour une salle existante
    public Salle updateSalle(Long salleId, Salle salleDetails) {
        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new RuntimeException("Salle introuvable"));
        salle.setNom(salleDetails.getNom());
        salle.setStatus(salleDetails.getStatus());
        return salleRepository.save(salle);
    }

    // Supprimer une salle
    public void deleteSalle(Long salleId) {
        if (!salleRepository.existsById(salleId)) {
            throw new RuntimeException("Salle introuvable");
        }
        salleRepository.deleteById(salleId);
    }

}
