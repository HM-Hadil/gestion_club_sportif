package esprit.gestion_club_sportif.services;

import esprit.gestion_club_sportif.entities.Role;
import esprit.gestion_club_sportif.entities.User;
import esprit.gestion_club_sportif.mappers.UserMapper;
import esprit.gestion_club_sportif.repo.IUserService;
import esprit.gestion_club_sportif.repo.UserRepo;
import esprit.gestion_club_sportif.request.UserRequest;
import esprit.gestion_club_sportif.response.UserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserMapper userMapper;
    private final UserRepo userRepo;


    //get user by id
    @Override
    public UserResult findUserById(UUID id) {
        return userMapper.entityToUser(userRepo.findById(id).orElse(null));
    }


    @Override
    public User getUserById(UUID id) {
        return userRepo.findById(id).orElse(null);
    }

    @Override
    public Optional<UserResult> findUserByEmail(String email) {
        var user=userRepo.findUserByEmail(email).orElseThrow();
                return Optional.ofNullable(userMapper.entityToUser(user));
    }

    @Override
    public UserResult updateUser(UUID id, UserRequest newUser) {
        // Retrieve the user from the repository
        var user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
       user.setFirstname(newUser.firstname());
       user.setLastname(newUser.lastname());
       user.setPhone(newUser.phone());
       userRepo.save(user);
        return userMapper.entityToUser(user);
    }
    public void save(User user) {
        userRepo.save(user);
    }
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
    @Override
    public List<User> getInactivePartners() {
        return userRepo.findByIsEnabledFalseAndRole(Role.Entreneur);
    }
    @Override
    public void deletePartnerAccount(UUID id) {
        userRepo.deleteById(id);
    }

    @Override
    public List<User> getActivatedPartners(){
        return userRepo.findByIsEnabledTrueAndRole(Role.Entreneur);
    }
}
