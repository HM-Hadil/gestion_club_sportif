package esprit.gestion_club_sportif.repo;

import esprit.gestion_club_sportif.entities.ConfirmationToken;
import esprit.gestion_club_sportif.request.AuthenticationRequest;
import esprit.gestion_club_sportif.request.UserRequest;
import esprit.gestion_club_sportif.response.AuthenticationResponse;
import jakarta.mail.MessagingException;


import java.net.MalformedURLException;
import java.util.UUID;

public interface IAuthService {
    AuthenticationResponse registerJoueur(UserRequest req) throws MalformedURLException, MessagingException;
    AuthenticationResponse authenticate(AuthenticationRequest req);

    AuthenticationResponse registerEntreneur(UserRequest req) throws MalformedURLException;





    void activateEntreneurAccount(UUID id) throws MessagingException;

    String confirmUserAccount(String token);


    ConfirmationToken createConfirmationToken(String email);
    void sendConfirmationToken(String email) throws MessagingException, MalformedURLException;


}
