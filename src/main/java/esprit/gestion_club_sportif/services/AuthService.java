package esprit.gestion_club_sportif.services;

import esprit.gestion_club_sportif.entities.ConfirmationToken;
import esprit.gestion_club_sportif.entities.Role;
import esprit.gestion_club_sportif.entities.User;
import esprit.gestion_club_sportif.exceptions.EmailAlreadyExistsException;
import esprit.gestion_club_sportif.exceptions.UserNotFoundException;
import esprit.gestion_club_sportif.repo.ConfirmationTokenRepository;
import esprit.gestion_club_sportif.repo.IAuthService;
import esprit.gestion_club_sportif.repo.IJwtService;
import esprit.gestion_club_sportif.repo.UserRepo;
import esprit.gestion_club_sportif.request.AuthenticationRequest;
import esprit.gestion_club_sportif.request.UserRequest;
import esprit.gestion_club_sportif.response.AuthenticationResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final IJwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Value("${jwt.expiration.minutes}")
    public int expirationMinutes; // 24 hours in minutes

    @Value("${app.url}")
    private String appUrl;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Override
    public AuthenticationResponse registerJoueur(UserRequest req) throws MalformedURLException {
        if (userRepo.existsByEmail(req.email())) {
            logger.warn("Attempt to register with existing email: {}", req.email());
            throw new EmailAlreadyExistsException("Email already exists!");
        }
        var user = esprit.gestion_club_sportif.entities.User.builder()
                .firstname(req.firstname())
                .lastname(req.lastname())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .phone(req.phone())
                .role(Role.Joueur) // Assign an appropriate role
                .isEnabled(true)
                .build();
        userRepo.save(user);

        var jwtToken = jwtService.generateToken(user);

        logger.info("Partner registered successfully: {}", req.email());
        return new AuthenticationResponse(jwtToken);
    }

    @Override
    public AuthenticationResponse registerEntreneur(UserRequest req) throws MalformedURLException {
        if (userRepo.existsByEmail(req.email())) {
            logger.warn("Attempt to register with existing email: {}", req.email());
            throw new EmailAlreadyExistsException("Email already exists!");
        }
        var user = User.builder()
                .firstname(req.firstname())
                .lastname(req.lastname())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .phone(req.phone())
                .role(Role.Entreneur)
                .isEnabled(false)
                .build();
        userRepo.save(user);

        var jwtToken = jwtService.generateToken(user);

        logger.info("User registered successfully: {}", req.email());
        return new AuthenticationResponse(jwtToken);
    }


    @Override
    public void activateEntreneurAccount(UUID id) throws jakarta.mail.MessagingException {
        var user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'email : " + id));

        if (user.isEnabled()) {
            throw new IllegalStateException("Le compte utilisateur est déjà activé.");
        }

        String newPassword = RandomStringUtils.randomAlphanumeric(10);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setEnabled(true);
        userRepo.save(user);

    }



    @Override
    public String confirmUserAccount(String token) {
        var confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElse(null);
        if (confirmationToken != null) {
            if (confirmationToken.isExpired()) {
                logger.warn("Expired confirmation token used: {}", token);
                return "Confirmation token expired!";
            }
            var user = confirmationToken.getUser();
            user.setEnabled(true);
            userRepo.save(user);
            confirmationTokenRepository.delete(confirmationToken); // Delete the token after use
            logger.info("User account confirmed: {}", user.getEmail());
            return "Compte vérifié avec succès !<br>Vous pouvez maintenant vous connecter en cliquant sur ce lien : <a href=\"" + appUrl + "/connexion\"><b>Se connecter</b></a>";
        }
        logger.error("Invalid confirmation token: {}", token);
        return "Error: Couldn't verify email!";
    }


    public AuthenticationResponse authenticate(AuthenticationRequest req) {
        try {
            logger.info("Attempting to authenticate user: {}", req.email());

            // Retrieve the user first to check if the account is enabled
            var user = userRepo.findByEmail(req.email())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Log the user's details
            logger.info("Retrieved user: {}", user);
            logger.info("User enabled status for {}: {}", req.email(), user.isEnabled());

            // Check if the account is not enabled
            if (!user.isEnabled()) {
                logger.info("Authentication attempt with unconfirmed account: {}", req.email());
                throw new RuntimeException("Account not confirmed. Veuillez d'abord activer votre compte via l'email que nous vous avons envoyé.");
            }

            // Proceed with authentication if the account is enabled
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.email(),
                            req.password()
                    )
            );

            // Generate JWT token
            var jwtToken = jwtService.generateToken(user);

            logger.info("User authenticated successfully: {}", req.email());
            return new AuthenticationResponse(jwtToken);

        } catch (UsernameNotFoundException e) {
            logger.error("User not found with email: {}", req.email(), e);
            throw new RuntimeException("User not found");

        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for user: {}", req.email(), e);
            throw new RuntimeException("Invalid credentials");

        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", req.email(), e);
            throw new RuntimeException("Authentication failed");
        }
    }

    public ConfirmationToken createConfirmationToken(String email) {
        var user = userRepo.findByEmail(email).orElseThrow();
        var token = jwtService.generateToken(user);

        var confirmationToken = ConfirmationToken.builder()
                .id(UUID.randomUUID())
                .token(token)
                .user(user)
                .expirationDate(LocalDateTime.now().plusMinutes(expirationMinutes))
                .build();
        return confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    public void sendConfirmationToken(String email) throws MessagingException, MalformedURLException {

    }


}