package esprit.gestion_club_sportif.services;


import esprit.gestion_club_sportif.entities.*;
import esprit.gestion_club_sportif.exceptions.*;
import esprit.gestion_club_sportif.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @Transactional
    public void annulerInscription(Long inscriptionId, UUID joueurId) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription non trouvée"));

        if (!inscription.getJoueur().getId().equals(joueurId)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à annuler cette inscription");
        }

        if (inscription.getSeance().getDateDebut().minusHours(24).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Impossible d'annuler moins de 24h avant la séance");
        }

        inscription.setStatut(StatutInscription.ANNULEE);
        inscriptionRepository.save(inscription);
    }

    @Transactional(readOnly = true)
    public List<Inscription> getInscriptionsJoueur(UUID joueurId) {
        return inscriptionRepository.findByJoueurIdOrderByDateInscriptionDesc(joueurId);
    }

    @Transactional(readOnly = true)
    public List<Inscription> getInscriptionsSeance(Long seanceId) {
        return inscriptionRepository.findBySeanceIdOrderByDateInscription(seanceId);
    }
}