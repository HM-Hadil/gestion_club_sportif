package esprit.gestion_club_sportif.request;


import esprit.gestion_club_sportif.entities.Role;

public record UserRequest(
        String firstname,
        String lastname,
        String email,
        String password,
        String phone,
        Role role
) {
}
