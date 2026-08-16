package ru.creditbank.loanmanagementservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http
  ) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)

        .sessionManagement(session -> session
            .sessionCreationPolicy(
                SessionCreationPolicy.STATELESS
            )
        )

        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/v3/api-docs.yaml",
                "/error"
            ).permitAll()

            .requestMatchers(HttpMethod.POST,
                "/loan-management-service/internal/api/v1/loans",
                "/loan-management-service/internal/api/v1/loans/{loanId}/schedule"
            )
            .hasAnyRole("USER", "CREDIT_MANAGER")

            .requestMatchers(HttpMethod.GET,
                "/loan-management-service/api/v1/payment/loans",
                "/loan-management-service/api/v1/payment/history",
                "/loan-management-service/internal/api/v1/credit-profile/user/{userId}"
            )
            .hasRole("USER")

            .requestMatchers(HttpMethod.POST,
                "/loan-management-service/api/v1/payment"
            )
            .hasRole("USER")

            .anyRequest().denyAll()
        )

        .oauth2ResourceServer(resourceServer ->
            resourceServer.jwt(
                Customizer.withDefaults()
            )
        )

        .build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter converter =
        new JwtGrantedAuthoritiesConverter();

    converter.setAuthoritiesClaimName("authorities");
    converter.setAuthorityPrefix("");

    JwtAuthenticationConverter authenticationConverter =
        new JwtAuthenticationConverter();

    authenticationConverter.setJwtGrantedAuthoritiesConverter(converter);

    return authenticationConverter;
  }
}