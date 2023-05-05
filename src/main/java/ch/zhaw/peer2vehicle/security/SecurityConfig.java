package ch.zhaw.peer2vehicle.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import ch.zhaw.peer2vehicle.repository.MieterRepository;

@Configuration
@EnableWebSecurity //Mit der Annotation @EnableWebSecurity wird spring-security aktiviert. 
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    String issuerUri;

    @Autowired
    MieterRepository mieterRepository;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
        In der filterChain wir festgelegt, welche Endpoints mit Authentisierung
        geschützt werden. Im Beispiel unten wird für alle Endpoints, die mit /api beginnen,
        Authentisierung verlangt
        */
        http.authorizeHttpRequests()
                .requestMatchers("/*").permitAll()
                .requestMatchers("/api/**").authenticated()
                .requestMatchers("/build/**").permitAll()
                /* Erklärung:/api/* matcht alle Pfade, die mit /api/ beginnen, aber keine weiteren Trennzeichen
                 * «/» enthalten. Weil die Service-Endpoints mit /api/service/ beginnen, und somit weitere
                 * Trennzeichen enthalten, müssen wir stattdessen /api/** (mit 2 *) verwenden. Dadurch werden
                 * sämtliche Pfade gematcht, die mit /api beginnen (egal, ob sie Trennzeichen enthalten oder nicht).*/
                .and().cors(withDefaults())
                .oauth2ResourceServer(server -> server.jwt()
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(new RoleExtractor()));             
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);
        OAuth2TokenValidator<Jwt> userValidator = new UserValidator(mieterRepository);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> myValidator = new DelegatingOAuth2TokenValidator<>(withIssuer, userValidator);
        jwtDecoder.setJwtValidator(myValidator);
        return jwtDecoder;
    }
}

