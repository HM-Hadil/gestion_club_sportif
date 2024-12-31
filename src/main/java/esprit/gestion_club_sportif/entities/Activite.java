package esprit.gestion_club_sportif.entities;

import esprit.gestion_club_sportif.entities.Seance;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Activite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @OneToMany(mappedBy = "activite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seance> seances;
}
