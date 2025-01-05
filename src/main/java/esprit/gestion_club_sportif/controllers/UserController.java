package esprit.gestion_club_sportif.controllers;

import esprit.gestion_club_sportif.entities.User;
import esprit.gestion_club_sportif.repo.IUserService;
import esprit.gestion_club_sportif.request.UserRequest;
import esprit.gestion_club_sportif.response.UserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    @Autowired
    private IUserService iUserService;
    @PutMapping("user/{id}")
    public ResponseEntity<UserResult> updateUser(@PathVariable UUID id, @RequestBody UserRequest user){

        return  ResponseEntity.ok(this.iUserService.updateUser(id,user));
    }
    @GetMapping("/inactive")
    public ResponseEntity<List<User>> getInactivePartners() {
        return ResponseEntity.ok( iUserService.getInactivePartners());
    }
    @GetMapping("/active")
    public ResponseEntity<List<User>> getActivePartners() {
        return ResponseEntity.ok( iUserService.getActivatedPartners());
    }
    @GetMapping("/user/id/{id}")
    public ResponseEntity<UserResult> getUserById(@PathVariable("id")  UUID id){

        return  ResponseEntity.ok(this.iUserService.findUserById(id));
    }
    @GetMapping("user/email/{email}")
    public ResponseEntity<Optional<UserResult>> getUserByemail(@PathVariable("email") String email){
        return  ResponseEntity.ok(this.iUserService.findUserByEmail(email));

    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deletePartner(@RequestParam UUID id) {
        try {
            iUserService.deletePartnerAccount(id);
            return ResponseEntity.ok("Partner account deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the account.");
        }
    }
}
