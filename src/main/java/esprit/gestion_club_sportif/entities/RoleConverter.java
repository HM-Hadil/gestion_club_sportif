package esprit.gestion_club_sportif.entities;
import jakarta.persistence.AttributeConverter;


public class RoleConverter implements AttributeConverter<Role, String> {
    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null) {
            return null;
        }
        return role.getDbCode();
    }

    @Override
    public Role convertToEntityAttribute(String dbCode) {
        return Role.fromDbCode(dbCode);
    }
}

