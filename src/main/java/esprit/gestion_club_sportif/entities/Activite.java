package esprit.gestion_club_sportif.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import esprit.gestion_club_sportif.entities.Seance;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Activite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entraineur_id")
    @JsonIgnore

    private User entraineur;
    @OneToMany(mappedBy = "activite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seance> seances = new ArrayList<>();

    // Helper method to add seance
    public void addSeance(Seance seance) {
        seances.add(seance);
        seance.setActivite(this);
    }

    public void removeSeance(Seance seance) {
        seances.remove(seance);
        seance.setActivite(null);
    }
}