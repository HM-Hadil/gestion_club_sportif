package esprit.gestion_club_sportif.repo;

import esprit.gestion_club_sportif.entities.Role;
import esprit.gestion_club_sportif.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findUserByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByIsEnabledFalseAndRole(Role role);


    List<User> findByIsEnabledTrueAndRole(Role partner);}