package esprit.gestion_club_sportif.services;

import esprit.gestion_club_sportif.entities.Seance;
import esprit.gestion_club_sportif.entities.StatusSeance;
import esprit.gestion_club_sportif.repo.SeanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeanceService {
    private final SeanceRepository seanceRepository;

    public SeanceService(SeanceRepository seanceRepository) {
        this.seanceRepository = seanceRepository;
    }

    // Créer une séance
    public Seance createSeance(Seance seance) {
        seance.setStatus(StatusSeance.DISPONIBLE); // Par défaut, une séance est disponible
        return seanceRepository.save(seance);
    }
    public List<Seance> getSeancesByStatus(StatusSeance status) {
        return seanceRepository.findByStatus(status);
    }




}
