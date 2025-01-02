package esprit.gestion_club_sportif.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvitationNotFoundException extends RuntimeException {
    public InvitationNotFoundException(Long invitationId) {
        super("Invitation not found with id: " + invitationId);
    }
}