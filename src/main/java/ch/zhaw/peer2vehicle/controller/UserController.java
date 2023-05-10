package ch.zhaw.peer2vehicle.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ch.zhaw.peer2vehicle.model.User;
import ch.zhaw.peer2vehicle.model.UserCreateDTO;
import ch.zhaw.peer2vehicle.model.MailInformation;
import ch.zhaw.peer2vehicle.repository.UserRepository;
import ch.zhaw.peer2vehicle.service.MailValidatorService;

import org.springframework.security.access.annotation.Secured; //Mit @Secured wird der Request auf die entsprechende Rolle beschränkt
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    MailValidatorService mailValidatorService;

    @PostMapping("/user")
    @Secured("ROLE_admin")
    public ResponseEntity<User> createUser(
            @RequestBody UserCreateDTO uDTO) {
        // Validate email using MailValidatorService
        MailInformation mailInformation = mailValidatorService.validateEmail(uDTO.getEmail());

        // Check if the email is valid and not from a temporary domain
        if (mailInformation.isFormat() && !mailInformation.isDisposable() && mailInformation.isDns()) { // Es ist is und nicht getFormat, da es in MailInformation.java booleans sind.
            User uDAO = new User(uDTO.getEmail(), uDTO.getName());
            User u = userRepository.save(uDAO);
            return new ResponseEntity<>(u, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user")
    @Secured("ROLE_admin")
    public ResponseEntity<Page<User>> getAllUser(
            @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "2") Integer pageSize) {
        Page<User> allUser = userRepository.findAll(PageRequest.of(pageNumber - 1, pageSize));
        return new ResponseEntity<>(allUser, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    @Secured("ROLE_admin")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Optional<User> optUser = userRepository.findById(id);
        // Falls die ID existiert, OK und den User zurückgeben
        // Falls die ID nicht existiert, NOT_FOUND zurückgeben
        if (optUser.isPresent()) {
            return new ResponseEntity<>(optUser.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/me/user")
    public ResponseEntity<User> assignToMe(@AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getClaimAsString("email");
        User user = userRepository.findFirstByEmail(userEmail);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/user/{id}")
    @Secured("ROLE_admin")
    public ResponseEntity<String> deleteUserById(@PathVariable String id) {
        Optional<User> userToDelete = userRepository.findById(id);
        if (userToDelete.isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("User with ID " + id + " has been deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " not found.");
        }
    }
}