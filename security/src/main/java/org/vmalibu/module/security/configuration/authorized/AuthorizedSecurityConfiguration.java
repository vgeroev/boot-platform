package org.vmalibu.module.security.configuration.authorized;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.vmalibu.module.security.authentication.jwt.JwtAuthenticationManager;
import org.vmalibu.module.security.authorization.manager.CustomAuthorizationManager;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class AuthorizedSecurityConfiguration {

    private final List<? extends CustomAuthorizationManager> authorizationManagers;
    private final JwtAuthenticationManager jwtAuthenticationManager;

    @Autowired
    public AuthorizedSecurityConfiguration(
            @NonNull @Autowired JwtAuthenticationManager jwtAuthenticationManager,
            @Nullable @Autowired(required = false) List<? extends CustomAuthorizationManager> authorizationManagers) {
        this.jwtAuthenticationManager = jwtAuthenticationManager;
        this.authorizationManagers = authorizationManagers == null ? List.of() : authorizationManagers;
    }

    @Bean
    SecurityFilterChain protectedFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/authorized/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.authenticationManager(jwtAuthenticationManager))
                )
                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(ar -> ar.anyRequest().authenticated())
                .addFilterBefore(new AuthorizationFilter(getAuthorizationManagers()), AuthorizationFilter.class);

        return http.build();
    }

    private AuthorizationManager<HttpServletRequest> getAuthorizationManagers() {
        if (authorizationManagers.isEmpty()) {
            log.warn("There are no authorization managers for protectedFilterChain. Using negative strategy for any request...");
            return (authentication, object) -> new AuthorizationDecision(false);
        } else {
            String simpleManagerClassNames = authorizationManagers.stream()
                            .map(m -> m.getClass().getName())
                            .collect(Collectors.joining(", "));
            log.info("Authorization managers for protectedFilterChain: [{}]", simpleManagerClassNames);
            return AuthorizationManagers.allOf(authorizationManagers.toArray(new CustomAuthorizationManager[0]));
        }
    }

    private CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        config.setAllowedMethods(List.of(CorsConfiguration.ALL));
        config.setAllowedHeaders(List.of(CorsConfiguration.ALL));
        config.setAllowedOrigins(List.of("http://localhost:3000", "https://localhost:3000"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
