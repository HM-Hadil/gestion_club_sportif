package esprit.gestion_club_sportif.controllers;


import esprit.gestion_club_sportif.entities.Activite;
import esprit.gestion_club_sportif.entities.Seance;
import esprit.gestion_club_sportif.request.ActiviteRequest;
import esprit.gestion_club_sportif.request.ActiviteResult;
import esprit.gestion_club_sportif.services.ActiviteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("activites")
@CrossOrigin(origins = "http://localhost:4200")

public class ActiviteController {
    private final ActiviteService activiteService;

    public ActiviteController(ActiviteService activiteService) {
        this.activiteService = activiteService;
    }



    @GetMapping
    public ResponseEntity<List<ActiviteResult>> getAllActivities() {
        return ResponseEntity.ok(activiteService.getAllActivities());
    }

    @GetMapping("/entraineur/{entraineurId}")
    public ResponseEntity<List<ActiviteResult>> getActivitiesByEntraineur(@PathVariable UUID entraineurId) {
        return ResponseEntity.ok(activiteService.getActivitiesByEntraineur(entraineurId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activite> getActiviteById(@PathVariable Long id) {
        return ResponseEntity.ok(activiteService.getActiviteById(id));
    }

    @PostMapping("/activites")
    public ResponseEntity<Activite> createActivite(@RequestBody ActiviteRequest activiteRequest) {
        Activite createdActivite = activiteService.createActivite(activiteRequest);
        return ResponseEntity.ok(createdActivite);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Entreneur')")
    public ResponseEntity<Activite> updateActivite(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Activite activite) {
        UUID entraineurId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(activiteService.updateActivite(id, entraineurId, activite));
    }

    @PutMapping("/{id}/seances")
    public ResponseEntity<Activite> updateActiviteSeances(
            @PathVariable Long id,

            @RequestBody List<Seance> seances,
            @RequestParam Long salleId) {

        return ResponseEntity.ok(activiteService.updateActiviteSeances(id, seances, salleId));
    }

    @DeleteMapping("/{activiteId}/seances/{seanceId}")
    public ResponseEntity<Void> deleteSeance(
            @PathVariable Long activiteId,
            @PathVariable Long seanceId

    ) {

        activiteService.deleteSeance(activiteId, seanceId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivite(@PathVariable Long id) {
        activiteService.deleteActivite(id);
        return ResponseEntity.noContent().build();
    }

}