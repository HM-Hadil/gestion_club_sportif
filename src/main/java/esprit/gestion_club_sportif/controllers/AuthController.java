package esprit.gestion_club_sportif.controllers;


import esprit.gestion_club_sportif.repo.IAuthService;
import esprit.gestion_club_sportif.request.AuthenticationRequest;
import esprit.gestion_club_sportif.request.UserRequest;
import esprit.gestion_club_sportif.response.AuthenticationResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.MalformedURLException;
import java.util.UUID;

@RestController
@RequestMapping("api/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService iAuthService;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerJoueur(@RequestBody UserRequest req) throws MalformedURLException, MessagingException {
        return ResponseEntity.ok(iAuthService.registerJoueur(req));

    }
    @PostMapping("/register/Entreneur")
    public ResponseEntity<AuthenticationResponse> registerEntreneur(@RequestBody UserRequest req) throws MalformedURLException {
        return ResponseEntity.ok(iAuthService.registerEntreneur(req));

    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest req) {
        return ResponseEntity.ok(iAuthService.authenticate(req));

    }


    @GetMapping("/confirm-account")
    public ResponseEntity<String> confirmUserAccount(@RequestParam String token) {
        String result = iAuthService.confirmUserAccount(token);
        if (result.contains("Error") || result.contains("expired")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok("<html><body><b>" + result + "</b></body></html>");
    }


    @PostMapping("/activateEntreneur")
    public ResponseEntity<String> activateEntreneur(@RequestParam UUID id) throws MessagingException {
        iAuthService.activateEntreneurAccount(id);
        return ResponseEntity.ok("Partner account activated successfully.");
    }



}