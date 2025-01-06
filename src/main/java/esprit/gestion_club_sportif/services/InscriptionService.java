package esprit.gestion_club_sportif.services;


import esprit.gestion_club_sportif.entities.*;
import esprit.gestion_club_sportif.exceptions.*;
import esprit.gestion_club_sportif.repo.*;
import esprit.gestion_club_sportif.response.InscriptionResult;
import esprit.gestion_club_sportif.response.SeanceDTO;
import esprit.gestion_club_sportif.response.SeanceResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InscriptionService {
    private final InscriptionRepository inscriptionRepository;
    private final SeanceRepository seanceRepository;
    private final UserRepo userRepository;

    @Transactional
    public Inscription inscrireJoueurASeance(Long seanceId, UUID joueurId) {
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Séance non trouvée"));

        User joueur = userRepository.findById(joueurId)
                .orElseThrow(() -> new ResourceNotFoundException("Joueur non trouvé"));

        // Vérifier que l'utilisateur a le rôle Joueur
        if (!joueur.getRole().equals(Role.Joueur)) {
            throw new UnauthorizedException("Seuls les joueurs peuvent s'inscrire aux séances");
        }

        // Vérifier si le joueur est déjà inscrit
        if (inscriptionRepository.existsBySeanceAndJoueur(seance, joueur)) {
            throw new DuplicateInscriptionException("Vous êtes déjà inscrit à cette séance");
        }

        // Vérifier s'il reste des places
        if (!seance.isPlacesDisponibles()) {
            throw new SeancePleineException("Cette séance est complète");
        }

        // Vérifier si la date de la séance n'est pas passée
        if (seance.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new SeancePasseeException("Cette séance a déjà commencé");
        }

        Inscription inscription = Inscription.builder()
                .seance(seance)
                .joueur(joueur)
                .dateInscription(LocalDateTime.now())
                .statut(StatutInscription.CONFIRMEE)
                .presenceConfirmee(false)
                .build();

        return inscriptionRepository.save(inscription);
    }

    public void confirmerPresence(Long inscriptionId) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription non trouvée"));

        // Mettre à jour le statut de présence
        inscription.setPresenceConfirmee(true);
        inscriptionRepository.save(inscription);
    }
    @Transactional
    public void annulerInscription(Long inscriptionId) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription non trouvée"));

        // Vérifier si l'inscription peut être annulée (par exemple, si la séance commence dans moins de 24 heures)
        if (inscription.getSeance().getDateDebut().minusHours(24).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Impossible d'annuler moins de 24h avant la séance");
        }

        // Annuler l'inscription
        inscription.setStatut(StatutInscription.ANNULEE);
        inscriptionRepository.save(inscription);
    }

    @Transactional(readOnly = true)
    public List<InscriptionResult> getInscriptionsJoueur(UUID joueurId) {
        List<Inscription> inscriptions = inscriptionRepository.findByJoueurIdOrderByDateInscriptionDesc(joueurId);
        return inscriptions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private InscriptionResult convertToDto(Inscription inscription) {
        InscriptionResult dto = new InscriptionResult();
        dto.setId(inscription.getId());

        // Récupérer la séance
        Seance seance = inscription.getSeance();
        if (seance != null) {
            // Récupérer l'activité liée à la séance et son nom
            Activite activite = seance.getActivite();
            if (activite != null) {
                dto.setSeanceName(activite.getNom()); // Assurez-vous que 'getNom()' existe dans Activite
            }

            dto.setDateInscription(inscription.getDateInscription());
            dto.setStatut(inscription.getStatut().name()); // Si vous voulez exposer le statut sous forme de string
            dto.setPresenceConfirmee(inscription.getPresenceConfirmee());
            dto.setCommentaire(inscription.getCommentaire());
            return dto;
        }
        return dto;
    }
    @Transactional(readOnly = true)
    public List<InscriptionResult> getInscriptionsSeance(Long seanceId) {
        // Changed to use the entity and convert it manually instead of relying on direct DTO projection
        List<Inscription> inscriptions = inscriptionRepository.findBySeanceIdOrderByDateInscriptionDesc(seanceId);
        return inscriptions.stream()
                .map(this::convertToInscriptionResult)
                .collect(Collectors.toList());
    }

    private InscriptionResult convertToInscriptionResult(Inscription inscription) {
        InscriptionResult result = new InscriptionResult();
        result.setId(inscription.getId());

        // Safely get the seance and its details
        Seance seance = inscription.getSeance();
        if (seance != null) {
            // Create a clean copy of the seance to avoid circular references
            Seance cleanSeance = new Seance();
            cleanSeance.setId(seance.getId());
            cleanSeance.setDateDebut(seance.getDateDebut());
            cleanSeance.setDateFin(seance.getDateFin());
            cleanSeance.setNombreLimite(seance.getNombreLimite());
            cleanSeance.setPrix(seance.getPrix());
            cleanSeance.setStatus(seance.getStatus());

            // Set salle if exists
            if (seance.getSalle() != null) {
                Salle salle = new Salle();
                salle.setId(seance.getSalle().getId());
                salle.setNom(seance.getSalle().getNom());
                salle.setStatus(seance.getSalle().getStatus());
                cleanSeance.setSalle(salle);
            }

            // Set activite if exists
            if (seance.getActivite() != null) {
                Activite activite = new Activite();
                activite.setId(seance.getActivite().getId());
                activite.setNom(seance.getActivite().getNom());
                // Don't set seances to avoid circular reference
                cleanSeance.setActivite(activite);
            }

            result.setSeance(cleanSeance);
            result.setSeanceName(seance.getActivite() != null ? seance.getActivite().getNom() : null);
        }

        result.setDateInscription(inscription.getDateInscription());
        result.setStatut(inscription.getStatut().name());
        result.setPresenceConfirmee(inscription.getPresenceConfirmee());
        result.setCommentaire(inscription.getCommentaire());

        if (inscription.getJoueur() != null) {
            result.setJoueurId(inscription.getJoueur().getId());
            // Add any other player details you need
        }

        return result;
    }

}


