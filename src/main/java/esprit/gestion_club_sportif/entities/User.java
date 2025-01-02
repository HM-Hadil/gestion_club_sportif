package esprit.gestion_club_sportif.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    @Column(name = "enabled")

    private boolean isEnabled;
    @Convert(converter = RoleConverter.class)
    private Role role;

    private String password;


    @OneToMany(mappedBy = "entraineur", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Activite> activites = new ArrayList<>();

    // Méthodes spécifiques pour le rôle Entraineur
    public boolean isEntraineur() {
        return role == Role.Entreneur;
    }

    public void addActivite(Activite activite) {
        activites.add(activite);
        activite.setEntraineur(this);
    }

    public void removeActivite(Activite activite) {
        activites.remove(activite);
        activite.setEntraineur(null);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String toString() {
        return this.firstname + " " + this.lastname;
    }
    }
