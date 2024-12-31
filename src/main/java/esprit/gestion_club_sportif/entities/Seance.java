package esprit.gestion_club_sportif.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Seance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    private int duree; // En minutes
    private int nombreLimite;

    private double prix;

    private String status; // Par exemple : "Disponible", "Complet", etc.

    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;


    @ManyToOne
    @JoinColumn(name = "activite_id")
    private Activite activite;
}
