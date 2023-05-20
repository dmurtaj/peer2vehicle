package ch.zhaw.peer2vehicle.security;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@TestConfiguration
public class TestSecurityConfig {
  // inspired by https://stackoverflow.com/questions/61500578/how-to-mock-jwt-authentication-in-a-spring-boot-unit-test

  static final String AUTH0_TOKEN = "token";
  static final String SUB = "sub";
  static final String AUTH0ID = "sms|12345678";

  @Bean
  public JwtDecoder jwtDecoder() {
    return new JwtDecoder() {
      @Override
      public Jwt decode(String token) {
        return jwt();
      }
    };
  }

  public Jwt jwt() {

    Map<String, Object> claims = new HashMap<>();
    claims.put(SUB, "test");
    claims.put("user_roles", Arrays.asList("admin"));

    return new Jwt(
        AUTH0_TOKEN,
        Instant.now(),
        Instant.now().plusSeconds(30),
        Map.of("alg", "none"),
        claims
    );
  }

}