package esprit.gestion_club_sportif.repo;
import esprit.gestion_club_sportif.entities.User;
import esprit.gestion_club_sportif.request.UserRequest;
import esprit.gestion_club_sportif.response.UserResult;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserService {
    UserResult findUserById(UUID id);
    UserResult updateUser(UUID id, UserRequest user);


    Optional<UserResult> findUserByEmail(String email);
    void deletePartnerAccount(UUID id);

    List<User> getInactivePartners();

    List<User> getActivatedPartners();
}
