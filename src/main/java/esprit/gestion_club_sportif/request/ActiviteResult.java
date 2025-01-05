package esprit.gestion_club_sportif.request;


import esprit.gestion_club_sportif.response.SeanceResult;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class ActiviteResult {
    private Long id;
    private String nom;
    private UUID entraineurId;
    private String entraineurFirstname;  // Prénom de l'entraîneur
    private String entraineurLastname;
    private List<SeanceResult> seances;
}