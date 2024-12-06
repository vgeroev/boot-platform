package org.vmalibu.module.security.configuration.authorized;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.vmalibu.module.security.authentication.jwt.JwtAuthenticationManager;
import org.vmalibu.module.security.authorization.manager.CustomAuthorizationManager;
import org.vmalibu.module.security.configuration.authorized.flow.AuthFlow;
import org.vmalibu.module.security.configuration.authorized.flow.AuthFlowRequestMatcher;
import org.vmalibu.module.security.configuration.authorized.flow.JwtAuthFlow;
import org.vmalibu.module.security.configuration.authorized.flow.SessionBasedAuthFlow;

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
    private final List<AuthFlow> authFlows;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthorizedSecurityConfiguration(
            JwtAuthenticationManager jwtAuthenticationManager,
            @Autowired(required = false) List<? extends CustomAuthorizationManager> authorizationManagers,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        this.jwtAuthenticationManager = jwtAuthenticationManager;
        this.authorizationManager = getAuthorizationManager(authorizationManagers == null ? List.of() : authorizationManagers);
        this.authFlows = List.of(new SessionBasedAuthFlow(), new JwtAuthFlow());
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    @Order(1)
    SecurityFilterChain sessionBasedFilterChain(HttpSecurity http) throws Exception {
        UsernamePasswordAuthenticationFilter authFilter = new UsernamePasswordAuthenticationFilter();
        authFilter.setAuthenticationSuccessHandler(
                (request, response, authentication) -> response.setStatus(HttpStatus.OK.value()));
        authFilter.setAuthenticationFailureHandler(
                (request, response, exception) -> response.setStatus(HttpStatus.UNAUTHORIZED.value()));
        authFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(PATH_LOGIN, "POST"));
        DaoAuthenticationProvider authenticationProvider = daoAuthProvider();
        authFilter.setAuthenticationManager(authenticationProvider::authenticate);
        authFilter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());

        DefaultSecurityFilterChain filterChain = http
                .securityMatcher(API_PATTERN)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
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
                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(ar -> ar.anyRequest().authenticated())
                .addFilterAt(authFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new AuthorizationFilter(authorizationManager), AuthorizationFilter.class)
                .build();

        return buildFilterChain(filterChain, SessionBasedAuthFlow.class, true);
    }

    private DaoAuthenticationProvider daoAuthProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    @Order(2)
    SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        DefaultSecurityFilterChain filterChain = http
                .securityMatcher(API_PATTERN)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.authenticationManager(jwtAuthenticationManager))
                )
                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(ar -> ar.anyRequest().authenticated())
                .addFilterBefore(new AuthorizationFilter(authorizationManager), AuthorizationFilter.class)
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
