package esprit.gestion_club_sportif.controllers;


import esprit.gestion_club_sportif.entities.Activite;
import esprit.gestion_club_sportif.entities.Seance;
import esprit.gestion_club_sportif.services.ActiviteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activites")
public class ActiviteController {
    private final ActiviteService activiteService;

    public ActiviteController(ActiviteService activiteService) {
        this.activiteService = activiteService;
    }

    @PostMapping("/create")
    public Activite createActiviteWithSeances(
            @RequestParam String nomActivite,
            @RequestBody List<Seance> seancesData,
            @RequestParam Long salleId) {

        return activiteService.createActiviteWithSeances(nomActivite, seancesData, salleId);
    }}