package esprit.gestion_club_sportif.request;

import esprit.gestion_club_sportif.entities.StatusSeance;

import java.time.LocalDateTime;

public class SeanceRequest {
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private int nombreLimite;
    private double prix;
    private StatusSeance status;
    private Long salleId;

    // Getters et Setters
    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public int getNombreLimite() {
        return nombreLimite;
    }

    public void setNombreLimite(int nombreLimite) {
        this.nombreLimite = nombreLimite;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public StatusSeance getStatus() {
        return status;
    }

    public void setStatus(StatusSeance status) {
        this.status = status;
    }

    public Long getSalleId() {
        return salleId;
    }

    public void setSalleId(Long salleId) {
        this.salleId = salleId;
    }
}
