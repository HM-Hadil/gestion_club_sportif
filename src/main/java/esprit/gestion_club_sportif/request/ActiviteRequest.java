package esprit.gestion_club_sportif.request;

import esprit.gestion_club_sportif.request.SeanceRequest;

import java.util.List;
import java.util.UUID;

public class ActiviteRequest {
    private String nom;
    private UUID entraineurId;
    private List<SeanceRequest> seances;

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public UUID getEntraineurId() {
        return entraineurId;
    }

    public void setEntraineurId(UUID entraineurId) {
        this.entraineurId = entraineurId;
    }

    public List<SeanceRequest> getSeances() {
        return seances;
    }

    public void setSeances(List<SeanceRequest> seances) {
        this.seances = seances;
    }
}
