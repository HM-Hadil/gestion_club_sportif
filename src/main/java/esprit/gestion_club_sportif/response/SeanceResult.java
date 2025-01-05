package esprit.gestion_club_sportif.response;

import esprit.gestion_club_sportif.entities.StatusSeance;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SeanceResult {
    private Long id;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private int nombreLimite;
    private double prix;
    private StatusSeance status;
    private SalleResult salle;
}