package esprit.gestion_club_sportif.services;


import esprit.gestion_club_sportif.entities.Activite;
import esprit.gestion_club_sportif.entities.Seance;
import esprit.gestion_club_sportif.entities.Salle;
import esprit.gestion_club_sportif.entities.User;
import esprit.gestion_club_sportif.exceptions.ResourceNotFoundException;
import esprit.gestion_club_sportif.exceptions.UnauthorizedException;
import esprit.gestion_club_sportif.repo.ActiviteRepository;
import esprit.gestion_club_sportif.repo.SalleRepository;
import esprit.gestion_club_sportif.repo.SeanceRepository;

import esprit.gestion_club_sportif.repo.UserRepo;
import esprit.gestion_club_sportif.request.ActiviteRequest;
import esprit.gestion_club_sportif.request.SeanceRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ActiviteService {
    private final ActiviteRepository activiteRepository;
    private final SeanceRepository seanceRepository;
    private final SalleRepository salleRepository;
    private final UserRepo userRepository;


    public ActiviteService(ActiviteRepository activiteRepository, SeanceRepository seanceRepository, SalleRepository salleRepository
    ,UserRepo userRepository) {
        this.activiteRepository = activiteRepository;
        this.seanceRepository = seanceRepository;
        this.salleRepository = salleRepository;
        this.userRepository=userRepository;
    }
    @Transactional
    public Activite createActivite(ActiviteRequest activiteRequest) {
        UUID entraineurId = activiteRequest.getEntraineurId();
        User entraineur = userRepository.findById(entraineurId)
                .orElseThrow(() -> new ResourceNotFoundException("Entraineur non trouvé"));

        if (!entraineur.isEntraineur()) {
            throw new UnauthorizedException("L'utilisateur n'est pas un entraineur");
        }

        Activite activite = new Activite();
        activite.setNom(activiteRequest.getNom());
        activite.setEntraineur(entraineur);

        for (SeanceRequest seanceRequest : activiteRequest.getSeances()) {
            Salle salle = salleRepository.findById(seanceRequest.getSalleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée"));

            // Vérification des conflits de dates pour la salle
            List<Seance> seancesExistantes = seanceRepository.findBySalle(salle);

            boolean conflit = seancesExistantes.stream().anyMatch(seanceExistante ->
                    seanceRequest.getDateDebut().isBefore(seanceExistante.getDateFin()) &&
                            seanceRequest.getDateFin().isAfter(seanceExistante.getDateDebut())
            );

            if (conflit) {
                throw new IllegalArgumentException("La salle " + salle.getNom() +
                        " est déjà réservée entre " +
                        seanceRequest.getDateDebut() + " et " + seanceRequest.getDateFin());
            }

            // Création de la séance si la salle est disponible
            Seance seance = new Seance();
            seance.setDateDebut(seanceRequest.getDateDebut());
            seance.setDateFin(seanceRequest.getDateFin());
            seance.setNombreLimite(seanceRequest.getNombreLimite());
            seance.setPrix(seanceRequest.getPrix());
            seance.setStatus(seanceRequest.getStatus());
            seance.setSalle(salle);

            activite.addSeance(seance);
        }

        return activiteRepository.save(activite);
    }

    @Transactional(readOnly = true)
    public List<Activite> getAllActivities() {
        return activiteRepository.findAll();
    }
    @Transactional(readOnly = true)
    public List<Activite> getActivitiesByEntraineur(UUID entraineurId) {
        return activiteRepository.findByEntraineurId(entraineurId);
    }
    @Transactional(readOnly = true)
    public Activite getActiviteById(Long id) {
        return activiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activité non trouvée avec l'ID: " + id));
    }


    @Transactional
    public Activite updateActivite(Long id, UUID entraineurId, Activite activiteDetails) {
        Activite activite = getActiviteById(id);

        if (!activite.getEntraineur().getId().equals(entraineurId)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier cette activité");
        }

        activite.setNom(activiteDetails.getNom());
        return activiteRepository.save(activite);
    }

    @Transactional
    public Activite updateActiviteSeances(Long id, UUID entraineurId, List<Seance> newSeances, Long salleId) {
        Activite activite = getActiviteById(id);

        if (!activite.getEntraineur().getId().equals(entraineurId)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier cette activité");
        }

        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new ResourceNotFoundException("Salle non trouvée"));

        activite.getSeances().clear();

        for (Seance seance : newSeances) {
            seance.setSalle(salle);
            activite.addSeance(seance);
        }

        return activiteRepository.save(activite);
    }

    @Transactional
    public void deleteActivite(Long id, UUID entraineurId) {
        Activite activite = getActiviteById(id);

        if (!activite.getEntraineur().getId().equals(entraineurId)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer cette activité");
        }

        boolean hasInscriptions = activite.getSeances().stream()
                .anyMatch(seance -> !seance.getInscriptions().isEmpty());

        if (hasInscriptions) {
            throw new IllegalStateException("Impossible de supprimer une activité avec des inscriptions");
        }

        activiteRepository.delete(activite);
    }
}
