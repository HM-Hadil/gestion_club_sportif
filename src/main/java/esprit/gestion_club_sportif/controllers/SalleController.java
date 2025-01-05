package esprit.gestion_club_sportif.controllers;


import esprit.gestion_club_sportif.entities.Salle;
import esprit.gestion_club_sportif.entities.SalleStatus;
import esprit.gestion_club_sportif.services.SalleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("salles")
@CrossOrigin(origins = "http://localhost:4200")

//@PreAuthorize("hasAnyRole('ADMIN', 'ENTRENEUR')")  // Ajouter cette ligne

public class SalleController {
    private final SalleService salleService;

    public SalleController(SalleService salleService) {
        this.salleService = salleService;
    }

    @GetMapping("/disponibles")
    public List<Salle> getAvailableSalles() {
        return salleService.getAvailableSalles();
    }


    @GetMapping("/check-availability/{salleId}")
    public ResponseEntity<Boolean> checkSalleAvailability(
            @PathVariable Long salleId,
            @RequestParam LocalDateTime dateDebut,
            @RequestParam LocalDateTime dateFin) {
        return ResponseEntity.ok(salleService.isSalleAvailable(salleId, dateDebut, dateFin));
    }

    // Récupérer toutes les salles
    @GetMapping
    public List<Salle> getAllSalles() {
        return salleService.getAllSalles();
    }

    // Créer une nouvelle salle
    @PostMapping
    public Salle createSalle(@RequestBody Salle salle) {
        return salleService.createSalle(salle);
    }

    // Mettre à jour une salle
    @PutMapping("/{id}")
    public ResponseEntity<Salle> updateSalle(
            @PathVariable Long id,
            @RequestBody Salle salle) {
        return ResponseEntity.ok(salleService.updateSalle(id, salle));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Salle> updateSalleStatus(
            @PathVariable Long id,
            @RequestParam SalleStatus status) {
        return ResponseEntity.ok(salleService.updateSalleStatus(id, status));
    }

    // Supprimer une salle
    @DeleteMapping("/{id}")
    public String deleteSalle(@PathVariable Long id) {
        salleService.deleteSalle(id);
        return "Salle supprimée avec succès";
    }
}
