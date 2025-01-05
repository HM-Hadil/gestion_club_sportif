package esprit.gestion_club_sportif.entities;

import lombok.NonNull;

public enum Role {
    ADMIN("A"),
    Joueur("J"),
    Entreneur("E");

    private final String dbCode;

    Role(@NonNull String dbCode) {
        this.dbCode = dbCode;
    }

    @NonNull
    public String getDbCode() {
        return dbCode;
    }

    @NonNull
    public static Role fromDbCode(@NonNull String dbCode) {
        for (var role : values()) {
            if (role.dbCode.equals(dbCode)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid dbCode '" + dbCode + "' for Role");
    }
    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(role)) { // Comparaison insensible à la casse
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }

}
