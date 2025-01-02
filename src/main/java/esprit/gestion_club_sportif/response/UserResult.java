package esprit.gestion_club_sportif.response;

import esprit.gestion_club_sportif.entities.Role;

import java.util.UUID;

public record UserResult(
         UUID id,
         String firstname,
         String lastname,
         String email,
         String phone,
         Role role
) {
}
