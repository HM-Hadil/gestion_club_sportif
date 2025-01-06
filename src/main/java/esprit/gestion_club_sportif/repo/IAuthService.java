package esprit.gestion_club_sportif.repo;

import esprit.gestion_club_sportif.entities.ConfirmationToken;
import esprit.gestion_club_sportif.request.AuthenticationRequest;
import esprit.gestion_club_sportif.request.UserRequest;
import esprit.gestion_club_sportif.response.AuthenticationResponse;
import jakarta.mail.MessagingException;


import java.net.MalformedURLException;
import java.util.UUID;

public interface IAuthService {
    AuthenticationResponse register(UserRequest req) throws MalformedURLException, MessagingException;
    AuthenticationResponse authenticate(AuthenticationRequest req);

    void activateEntreneurAccount(UUID id) throws MessagingException;





}
