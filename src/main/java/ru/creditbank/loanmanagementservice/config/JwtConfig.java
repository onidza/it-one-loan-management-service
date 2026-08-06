package ru.creditbank.loanmanagementservice.config;

import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import ru.creditbank.loanmanagementservice.property.JwtProperties;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

  private final JwtProperties jwtProperties;

  @Bean
  public SecretKey jwtSecretKey() {
    byte[] keyBytes = Base64.getDecoder()
        .decode(jwtProperties.secret());

    return new SecretKeySpec(
        keyBytes,
        "HmacSHA256"
    );
  }

  @Bean
  public JwtDecoder jwtDecoder(SecretKey secretKey) {
    NimbusJwtDecoder decoder = NimbusJwtDecoder
        .withSecretKey(secretKey)
        .macAlgorithm(MacAlgorithm.HS256)
        .build();

    decoder.setJwtValidator(
        JwtValidators.createDefaultWithIssuer(
            jwtProperties.issuer()
        )
    );

    return decoder;
  }
}
