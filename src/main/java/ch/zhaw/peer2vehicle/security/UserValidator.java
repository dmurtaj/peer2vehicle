package ch.zhaw.peer2vehicle.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import ch.zhaw.peer2vehicle.model.User;
import ch.zhaw.peer2vehicle.repository.UserRepository;

/*
Erklärungen zur Datei UserValidator: In unserer Anwendung verwenden wird die Rollen
«User» und «Mitarbeiter». User brauchen nicht nur einen Login bei Auth0, sondern
müssen auch als User in unserer DB erfasst sein. Wir wählen einen pragmatischen Ansatz
und erzeugen automatisch einen neuen User, wenn es für den eingeloggten User noch
keinen gibt. Das Matching zwischen dem Auth0-User und dem User erfolgt über das E-Mail.
*/

class UserValidator implements OAuth2TokenValidator<Jwt> {

    UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        
        OAuth2Error error = new OAuth2Error("invalid_token", "The required email is missing", null);

        String userEmail = jwt.getClaimAsString("email"); //E-Mail wird aus JWT gelesen

        //List<String> userRoles = jwt.getClaimAsStringList("user_roles");

        if (userEmail != null && !userEmail.equals("")) { 
            User m = userRepository.findFirstByEmail(userEmail);
            if (m==null) {     
                String username = jwt.getClaimAsString("nickname");
                userRepository.save(new User(userEmail, username)); //User wird automatisch erzeugt
            }
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(error);
    }
}


