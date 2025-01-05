package esprit.gestion_club_sportif.services;

import esprit.gestion_club_sportif.entities.Salle;
import esprit.gestion_club_sportif.entities.SalleStatus;
import esprit.gestion_club_sportif.entities.Seance;
import esprit.gestion_club_sportif.repo.SalleRepository;
import esprit.gestion_club_sportif.repo.SeanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalleService {
    private final SalleRepository salleRepository;
    private final SeanceRepository seanceRepository;

    public SalleService(SalleRepository salleRepository, SeanceRepository seanceRepository) {
        this.salleRepository = salleRepository;
        this.seanceRepository = seanceRepository;
    }

    /**
     * Récupère toutes les salles
     */
    public List<Salle> getAllSalles() {
        return salleRepository.findAll();
    }

    /**
     * Récupère les salles disponibles
     */
    public List<Salle> getAvailableSalles() {
        return salleRepository.findByStatus(SalleStatus.DISPONIBLE);
    }

    /**
     * Met à jour le statut d'une salle
     */
    @Transactional
    public Salle updateSalleStatus(Long salleId, SalleStatus newStatus) {
        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new RuntimeException("Salle non trouvée avec l'ID: " + salleId));

        salle.setStatus(newStatus);
        return salleRepository.save(salle);
    }

    /**
     * Vérifie si une salle est disponible pour une période donnée
     */
    public boolean isSalleAvailable(Long salleId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        // Vérifier que la salle existe
        if (!salleRepository.existsById(salleId)) {
            throw new RuntimeException("Salle non trouvée avec l'ID: " + salleId);
        }

        // Vérifier que les dates sont valides
        if (dateDebut.isAfter(dateFin)) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        // Récupérer toutes les séances pour cette salle
        List<Seance> seances = seanceRepository.findBySalleId(salleId);

        // Vérifier s'il y a des chevauchements avec les séances existantes
        return seances.stream().noneMatch(seance ->
                // Vérifie s'il y a chevauchement
                (dateDebut.isBefore(seance.getDateFin()) &&
                        dateFin.isAfter(seance.getDateDebut()))
        );
    }

    /**
     * Crée une nouvelle salle
     */
    @Transactional
    public Salle createSalle(Salle salle) {
        // Vérifier si une salle avec le même nom existe déjà
        Optional<Salle> existingSalle = salleRepository.findByNom(salle.getNom());
        if (existingSalle.isPresent()) {
            throw new RuntimeException("Une salle avec le nom '" + salle.getNom() + "' existe déjà.");
        }

        // S'assurer que l'ID est null pour une nouvelle salle
        salle.setId(null);

        // Définir le statut par défaut si non spécifié
        if (salle.getStatus() == null) {
            salle.setStatus(SalleStatus.DISPONIBLE);
        }

        return salleRepository.save(salle);
    }

    /**
     * Met à jour une salle existante
     */
    @Transactional
    public Salle updateSalle(Long salleId, Salle salleDetails) {
        return salleRepository.findById(salleId)
                .map(salle -> {
                    // Vérifier si une autre salle avec le même nom existe déjà
                    salleRepository.findByNom(salleDetails.getNom())
                            .filter(s -> !s.getId().equals(salleId))
                            .ifPresent(s -> {
                                throw new RuntimeException("Une salle avec le nom '" +
                                        salleDetails.getNom() + "' existe déjà.");
                            });

                    // Mettre à jour les informations de la salle
                    salle.setNom(salleDetails.getNom());
                    if (salleDetails.getStatus() != null) {
                        salle.setStatus(salleDetails.getStatus());
                    }

                    return salleRepository.save(salle);
                })
                .orElseThrow(() -> new RuntimeException("Salle non trouvée avec l'ID: " + salleId));
    }

    /**
     * Supprime une salle
     */
    @Transactional
    public void deleteSalle(Long salleId) {
        // Vérifier si la salle existe
        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new RuntimeException("Salle non trouvée avec l'ID: " + salleId));

        // Vérifier si la salle a des séances programmées
        List<Seance> seances = seanceRepository.findBySalleId(salleId);
        if (!seances.isEmpty()) {
            throw new RuntimeException("Impossible de supprimer la salle car elle a des séances programmées");
        }

        // Si la salle n'a pas de séances programmées, supprimer la salle
        salleRepository.delete(salle);
    }
}