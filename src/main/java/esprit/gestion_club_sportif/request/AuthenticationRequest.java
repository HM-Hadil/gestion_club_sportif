package esprit.gestion_club_sportif.request;

public record AuthenticationRequest(
        String email,
        String password
) {
}
