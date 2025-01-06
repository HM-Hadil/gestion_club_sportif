package esprit.gestion_club_sportif.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Seance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private int nombreLimite;
    private double prix;
    private int placesDisponibles;

    @Enumerated(EnumType.STRING)
    private StatusSeance status;

    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    @ManyToOne
    @JoinColumn(name = "activite_id")
    private Activite activite;

    @OneToMany(mappedBy = "seance", cascade = CascadeType.ALL)
    private Set<Inscription> inscriptions = new HashSet<>();

    @PrePersist
    public void prePersist() {
        this.placesDisponibles = this.nombreLimite;
    }

    public boolean isPlacesDisponibles() {
        return placesDisponibles > 0;
    }

   }