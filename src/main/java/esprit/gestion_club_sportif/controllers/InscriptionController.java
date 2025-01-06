package esprit.gestion_club_sportif.controllers;

import esprit.gestion_club_sportif.entities.Inscription;
import esprit.gestion_club_sportif.exceptions.UnauthorizedException;
import esprit.gestion_club_sportif.response.InscriptionResult;
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

    @PostMapping("/seances/{seanceId}/{joueurId}")
    public ResponseEntity<Inscription> inscrireASeance(
            @PathVariable Long seanceId,
            @PathVariable UUID joueurId
        ) {



        return ResponseEntity.ok(inscriptionService.inscrireJoueurASeance(seanceId, joueurId));
    }


    @PutMapping("/{inscriptionId}/annuler")
    public ResponseEntity<Void> annulerInscription(@PathVariable Long inscriptionId) {
        inscriptionService.annulerInscription(inscriptionId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/mes-inscriptions/{joueurId}")
    public ResponseEntity<List<InscriptionResult>> getMesInscriptions(
            @PathVariable UUID joueurId) {

        return ResponseEntity.ok(inscriptionService.getInscriptionsJoueur(joueurId));
    }
    @PutMapping("/{inscriptionId}/confirmerPresence")
    public ResponseEntity<Void> confirmerPresence(@PathVariable Long inscriptionId) {
        inscriptionService.confirmerPresence(inscriptionId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/seances/{seanceId}")
    @PreAuthorize("hasAnyRole('Entreneur', 'ADMIN')")
    public ResponseEntity<List<Inscription>> getInscriptionsSeance(@PathVariable Long seanceId) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsSeance(seanceId));
    }
    @GetMapping("/entraineur/{entraineurId}/seance/{seanceId}")
    public ResponseEntity<List<InscriptionResult>> getInscriptionsByEntraineur(
            @PathVariable Long seanceId,
            @PathVariable  UUID entraineurId ){
        return ResponseEntity.ok(inscriptionService.getInscriptionsByEntraineur(entraineurId, seanceId));
    }

}