package esprit.gestion_club_sportif.response;
import esprit.gestion_club_sportif.entities.Seance;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InscriptionResult {
    private Long id;
    private String seanceName;
    private Seance seance;



    private LocalDateTime dateInscription;
    private String statut;
    private Boolean presenceConfirmee;
    private String commentaire;
}
