package esprit.gestion_club_sportif.entities;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    private SalleStatus status = SalleStatus.DISPONIBLE; // Par défaut disponible
}
