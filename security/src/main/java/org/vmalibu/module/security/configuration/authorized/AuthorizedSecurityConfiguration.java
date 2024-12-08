package org.vmalibu.module.security.configuration.authorized;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.vmalibu.module.security.authentication.jwt.JwtAuthenticationManager;
import org.vmalibu.module.security.authorization.manager.CustomAuthorizationManager;
import org.vmalibu.module.security.configuration.authorized.filter.ExtraAuthSessionFilters;
import org.vmalibu.module.security.configuration.authorized.flow.AuthFlow;
import org.vmalibu.module.security.configuration.authorized.flow.AuthFlowRequestMatcher;
import org.vmalibu.module.security.configuration.authorized.flow.JwtAuthFlow;
import org.vmalibu.module.security.configuration.authorized.flow.SessionBasedAuthFlow;
import org.vmalibu.module.security.configuration.authorized.session.SendErrorAuthenticationFailureHandler;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class AuthorizedSecurityConfiguration {

    private static final String API_PATTERN = "/authorized/**";
    public static final String PATH_LOGIN = "/authorized/login";
    public static final String PATH_LOGOUT = "/authorized/logout";

    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final AuthorizationManager<HttpServletRequest> authorizationManager;
    private final ExtraAuthSessionFilters extraAuthSessionFilters;
    private final List<AuthFlow> authFlows;

    @Autowired
    public AuthorizedSecurityConfiguration(
            JwtAuthenticationManager jwtAuthenticationManager,
            @Autowired(required = false) List<? extends CustomAuthorizationManager> authorizationManagers,
            ExtraAuthSessionFilters extraAuthSessionFilters) {
        this.jwtAuthenticationManager = jwtAuthenticationManager;
        this.authorizationManager = getAuthorizationManager(authorizationManagers == null ? List.of() : authorizationManagers);
        this.extraAuthSessionFilters = extraAuthSessionFilters;
        this.authFlows = List.of(new SessionBasedAuthFlow(), new JwtAuthFlow());
    }

    @Bean
    // Without this Bean session management will not work
    HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    @Order(1)
    SecurityFilterChain sessionBasedFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(API_PATTERN)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .securityContext(s -> s.securityContextRepository(new HttpSessionSecurityContextRepository()))
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                .sessionAuthenticationFailureHandler(new SendErrorAuthenticationFailureHandler())
                                .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession)
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(true)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher(PATH_LOGOUT, "POST"))
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                        .addLogoutHandler(
                                new HeaderWriterLogoutHandler(
                                        new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.COOKIES)
                                )
                        )
                )
                .anonymous(AbstractHttpConfigurer::disable);

        AntPathRequestMatcher loginRequestMatcher = new AntPathRequestMatcher(PATH_LOGIN, "POST");
        DefaultSecurityFilterChain filterChain = extraAuthSessionFilters.addFilters(http, loginRequestMatcher)
                        .addFilter(getAuthorizationFilter())
                        .build();

        return buildFilterChain(filterChain, SessionBasedAuthFlow.class, true);
    }

    @Bean
    @Order(2)
    SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        DefaultSecurityFilterChain filterChain = http
                .securityMatcher(API_PATTERN)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.authenticationManager(jwtAuthenticationManager))
                )
                .anonymous(AbstractHttpConfigurer::disable)
                .addFilter(getAuthorizationFilter())
                .build();

        return buildFilterChain(filterChain, JwtAuthFlow.class, false);
    }

    private DefaultSecurityFilterChain buildFilterChain(DefaultSecurityFilterChain filterChain,
                                                        Class<? extends AuthFlow> authFlow,
                                                        boolean defaultAuthFlow) {
        RequestMatcher requestMatcher = filterChain.getRequestMatcher();
        AuthFlowRequestMatcher newRequestMatcher = new AuthFlowRequestMatcher(requestMatcher, authFlows, authFlow, defaultAuthFlow);
        return new DefaultSecurityFilterChain(newRequestMatcher, filterChain.getFilters());
    }

    private AuthorizationFilter getAuthorizationFilter() {
        return new AuthorizationFilter(authorizationManager);
    }

    private static AuthorizationManager<HttpServletRequest> getAuthorizationManager(
            List<? extends CustomAuthorizationManager> authorizationManagers
    ) {
        if (authorizationManagers.isEmpty()) {
            log.warn("There are no authorization managers for protectedFilterChain. Using forbid strategy for every request...");
            return (authentication, object) -> new AuthorizationDecision(false);
        } else {
            String authManagerClassNames = authorizationManagers.stream()
                            .map(m -> m.getClass().getName())
                            .collect(Collectors.joining(", "));
            log.info("Authorization managers for protectedFilterChains: [{}]", authManagerClassNames);
            return AuthorizationManagers.allOf(authorizationManagers.toArray(new CustomAuthorizationManager[0]));
        }
    }

}
