package ch.zhaw.peer2vehicle.security;

import java.util.List;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import ch.zhaw.peer2vehicle.model.Mieter;
import ch.zhaw.peer2vehicle.repository.MieterRepository;

/*
Erklärungen zur Datei UserValidator: In unserer Anwendung verwenden wird die Rollen
«Mieter» und «Mitarbeiter». Mieter brauchen nicht nur einen Login bei Auth0, sondern
müssen auch als Mieter in unserer DB erfasst sein. Wir wählen einen pragmatischen Ansatz
und erzeugen automatisch einen neuen Mieter, wenn es für den eingeloggten User noch
keinen gibt. Das Matching zwischen dem Auth0-User und dem Mieter erfolgt über das E-Mail.
*/

class UserValidator implements OAuth2TokenValidator<Jwt> {

    MieterRepository mieterRepository;

    public UserValidator(MieterRepository mieterRepository) {
        this.mieterRepository = mieterRepository;
    }

    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        OAuth2Error error = new OAuth2Error("invalid_token", "The required email is missing", null);

        String userEmail = jwt.getClaimAsString("email"); //E-Mail wird aus JWT gelesen
        List<String> userRoles = jwt.getClaimAsStringList("user_roles");
        if (userEmail != null && !userEmail.equals("")) { 
            Mieter m = mieterRepository.findFirstByEmail(userEmail);
            if (m==null && (userRoles==null || userRoles.isEmpty())) {     
                String username = jwt.getClaimAsString("nickname");
                mieterRepository.save(new Mieter(userEmail, username)); //Mieter wird automatisch erzeugt
            }
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(error);
    }
}


