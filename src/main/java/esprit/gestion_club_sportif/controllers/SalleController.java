package esprit.gestion_club_sportif.controllers;


import esprit.gestion_club_sportif.entities.Salle;
import esprit.gestion_club_sportif.entities.SalleStatus;
import esprit.gestion_club_sportif.services.SalleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salles")
public class SalleController {
    private final SalleService salleService;

    public SalleController(SalleService salleService) {
        this.salleService = salleService;
    }

    @GetMapping("/disponibles")
    public List<Salle> getAvailableSalles() {
        return salleService.getAvailableSalles();
    }

    @PutMapping("/{id}/status")
    public Salle updateSalleStatus(@PathVariable Long id, @RequestParam SalleStatus status) {
        return salleService.updateSalleStatus(id, status);
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
    public Salle updateSalle(@PathVariable Long id, @RequestBody Salle salle) {
        return salleService.updateSalle(id, salle);
    }

    // Supprimer une salle
    @DeleteMapping("/{id}")
    public String deleteSalle(@PathVariable Long id) {
        salleService.deleteSalle(id);
        return "Salle supprimée avec succès";
    }
}
