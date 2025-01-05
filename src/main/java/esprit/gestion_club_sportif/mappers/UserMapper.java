package esprit.gestion_club_sportif.mappers;


import esprit.gestion_club_sportif.entities.User;
import esprit.gestion_club_sportif.response.UserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {

    public UserResult entityToUser(User user) {
        if (user == null) {
            return null;
        }

        return new UserResult(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );

    }}