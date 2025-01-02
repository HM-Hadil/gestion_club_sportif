package esprit.gestion_club_sportif.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "confirmation_tokens", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationToken {
    @Value("${jwt.expiration.minutes}")
    public  int expirationMinutes ; // 24 hours in minutes

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String token;

    @Column(name = "expiration_date")

    @NotNull
    private LocalDateTime expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private Boolean used = Boolean.FALSE;

    public ConfirmationToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expirationDate = calculateExpiryDate();
    }

    private  LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusMinutes(expirationMinutes);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationDate);
    }
}
