package esprit.gestion_club_sportif.controllers;

import esprit.gestion_club_sportif.entities.Inscription;
import esprit.gestion_club_sportif.services.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/inscriptions")
@CrossOrigin("*")
@RequiredArgsConstructor
public class InscriptionController {
    private final InscriptionService inscriptionService;

    @PostMapping("/seances/{seanceId}")
    @PreAuthorize("hasRole('Joueur')")
    public ResponseEntity<Inscription> inscrireASeance(
            @PathVariable Long seanceId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID joueurId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(inscriptionService.inscrireJoueurASeance(seanceId, joueurId));
    }

    @DeleteMapping("/{inscriptionId}")
    @PreAuthorize("hasRole('Joueur')")
    public ResponseEntity<Void> annulerInscription(
            @PathVariable Long inscriptionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID joueurId = UUID.fromString(userDetails.getUsername());
        inscriptionService.annulerInscription(inscriptionId, joueurId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mes-inscriptions")
    @PreAuthorize("hasRole('Joueur')")
    public ResponseEntity<List<Inscription>> getMesInscriptions(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID joueurId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(inscriptionService.getInscriptionsJoueur(joueurId));
    }

    @GetMapping("/seances/{seanceId}")
    @PreAuthorize("hasAnyRole('Entreneur', 'ADMIN')")
    public ResponseEntity<List<Inscription>> getInscriptionsSeance(@PathVariable Long seanceId) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsSeance(seanceId));
    }
}