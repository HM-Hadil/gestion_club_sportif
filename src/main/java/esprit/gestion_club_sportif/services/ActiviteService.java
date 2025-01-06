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
import esprit.gestion_club_sportif.request.ActiviteResult;
import esprit.gestion_club_sportif.request.SeanceRequest;
import esprit.gestion_club_sportif.response.UserResult;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActiviteService {
    private final ActiviteRepository activiteRepository;
    private final SeanceRepository seanceRepository;
    private final SalleRepository salleRepository;
    private final UserRepo userRepository;

    private final ModelMapper modelMapper;
    @Autowired
    private final UserService userService;

    public ActiviteService(ActiviteRepository activiteRepository, ModelMapper modelMapper, SeanceRepository seanceRepository, SalleRepository salleRepository
    , UserRepo userRepository, UserService userService) {
        this.activiteRepository = activiteRepository;
        this.seanceRepository = seanceRepository;
        this.salleRepository = salleRepository;
        this.userRepository=userRepository;
        this.modelMapper=modelMapper;
        this.userService = userService;
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

            // Vérifier toutes les séances existantes pour cette salle
            List<Seance> seancesExistantes = seanceRepository.findAll().stream()
                    .filter(s -> s.getSalle().getId().equals(salle.getId()))
                    .toList();

            for (Seance seanceExistante : seancesExistantes) {
                if (seanceRequest.getDateDebut().equals(seanceExistante.getDateDebut()) ||
                        seanceRequest.getDateFin().equals(seanceExistante.getDateFin()) ||
                        (seanceRequest.getDateDebut().isAfter(seanceExistante.getDateDebut()) &&
                                seanceRequest.getDateDebut().isBefore(seanceExistante.getDateFin())) ||
                        (seanceRequest.getDateFin().isAfter(seanceExistante.getDateDebut()) &&
                                seanceRequest.getDateFin().isBefore(seanceExistante.getDateFin())) ||
                        (seanceRequest.getDateDebut().isBefore(seanceExistante.getDateDebut()) &&
                                seanceRequest.getDateFin().isAfter(seanceExistante.getDateFin()))) {

                    throw new IllegalArgumentException(
                            String.format("La salle est déjà réservée pour le créneau du %s %s au %s %s",
                                    seanceExistante.getDateDebut().toLocalDate(),
                                    seanceExistante.getDateDebut().toLocalTime(),
                                    seanceExistante.getDateFin().toLocalDate(),
                                    seanceExistante.getDateFin().toLocalTime())
                    );
                }
            }

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
    public List<ActiviteResult> getAllActivities() {
        List<Activite> activites = activiteRepository.findAll();
        return activites.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }




    private ActiviteResult convertToDto(Activite activite) {
        // Utilisation de modelMapper pour le mapping de base
        ActiviteResult dto = modelMapper.map(activite, ActiviteResult.class);

        // Si l'ID de l'entraîneur est disponible, nous récupérons l'entraîneur avec ce service
        if (activite.getEntraineur() != null) {
            dto.setEntraineurId(activite.getEntraineur().getId());

            // Appel au service User pour récupérer l'entraîneur complet par son ID
            UserResult entraineur = userService.findUserById(activite.getEntraineur().getId());
            if (entraineur != null) {
                dto.setEntraineurFirstname(entraineur.firstname());
                dto.setEntraineurLastname(entraineur.lastname());
            }
        }

        return dto;
    }


    @Transactional(readOnly = true)
    public List<ActiviteResult> getActivitiesByEntraineur(UUID entraineurId) {
        List<Activite> activites = activiteRepository.findByEntraineurId(entraineurId);
        return activites.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
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
    public Activite updateActiviteSeances(Long id,  List<Seance> newSeances, Long salleId) {
        Activite activite = getActiviteById(id);


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
    public void deleteSeance(Long activiteId, Long seanceId) {
        Activite activite = getActiviteById(activiteId);



        Seance seance = activite.getSeances().stream()
                .filter(s -> s.getId().equals(seanceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Séance non trouvée"));

        if (!seance.getInscriptions().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer une séance avec des inscriptions");
        }

        activite.getSeances().remove(seance);
        seanceRepository.delete(seance);
    }
    @Transactional
    public void deleteActivite(Long id) {
        Activite activite = activiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activité non trouvée avec l'ID: " + id));

        // Optionnel : vérifier si des contraintes supplémentaires doivent être prises en compte avant suppression
        if (activite.getSeances() != null && !activite.getSeances().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer une activité avec des séances associées");
        }

        activiteRepository.delete(activite);
    }
}
