package com.sangkhim.spring_boot3_keycloak.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  public static final String ADMIN = "admin";
  public static final String USER = "user";
  private final JwtAuthConverter jwtAuthConverter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // h2-console
    http.authorizeHttpRequests()
        .requestMatchers(toH2Console())
        .permitAll()
        .and()
        .csrf()
        .ignoringRequestMatchers(toH2Console())
        .and()
        .headers()
        .frameOptions()
        .sameOrigin();

    http.authorizeHttpRequests()
        .requestMatchers(HttpMethod.GET, "/swagger-ui/**", "/v3/api-docs/**", "/actuator/**")
        .permitAll()
        .requestMatchers(HttpMethod.GET, "/v1/authors/**", "/v1/tags/**")
        .hasRole(ADMIN)
        .requestMatchers(HttpMethod.GET, "/v1/**")
        .hasAnyRole(ADMIN, USER)
        .anyRequest()
        .authenticated();
    http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthConverter);
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    // Exception handling
    http.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint());
    http.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler());

    return http.build();
  }
}
