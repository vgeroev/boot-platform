package org.vmalibu.module.security.configuration.authorized.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
/*
  We need these filters because of after authenticating in UsernamePasswordAuthenticationFilter
  SessionManagementFilter does not fire
 */
public class ExtraAuthSessionFilters {

    public static final String AUTH_PASSED = ExtraAuthSessionFilters.class.getName() + ".AUTH_PASSED";

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    private static class ExtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

        private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
                .getContextHolderStrategy();

        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
            Authentication authentication = super.attemptAuthentication(request, response);
            if (authentication != null) {
                SecurityContext context = securityContextHolderStrategy.createEmptyContext();
                context.setAuthentication(authentication);
                securityContextHolderStrategy.setContext(context);
            }

            return authentication;
        }

        @Override
        protected void successfulAuthentication(HttpServletRequest request,
                                                HttpServletResponse response,
                                                FilterChain chain,
                                                Authentication authResult) throws IOException, ServletException {
            Boolean authPassed = (Boolean) request.getAttribute(AUTH_PASSED);
            if (Boolean.TRUE.equals(authPassed)) {
                super.successfulAuthentication(request, response, chain, authResult);
            }
        }
    }

    private static class CheckingAuthenticationFilter extends OncePerRequestFilter {

        private final RequestMatcher requestMatcher;

        CheckingAuthenticationFilter(RequestMatcher requestMatcher) {
            this.requestMatcher = requestMatcher;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            if (requestMatcher.matches(request)) {
                request.setAttribute(AUTH_PASSED, Boolean.TRUE);
                return;
            }

            filterChain.doFilter(request, response);
        }
    }

    public @NonNull HttpSecurity addFilters(@NonNull HttpSecurity httpSecurity,
                                            @NonNull RequestMatcher loginMatcher) {
        ExtUsernamePasswordAuthenticationFilter authFilter = new ExtUsernamePasswordAuthenticationFilter();
        authFilter.setContinueChainBeforeSuccessfulAuthentication(true);
        authFilter.setAuthenticationSuccessHandler(
                (request, response, authentication) -> response.setStatus(HttpStatus.OK.value()));
        authFilter.setAuthenticationFailureHandler(
                (request, response, exception) -> response.setStatus(HttpStatus.UNAUTHORIZED.value()));
        authFilter.setRequiresAuthenticationRequestMatcher(loginMatcher);
        DaoAuthenticationProvider authenticationProvider = daoAuthProvider();
        authFilter.setAuthenticationManager(authenticationProvider::authenticate);
        authFilter.setSecurityContextRepository(new NullSecurityContextRepository());

        return httpSecurity
                .addFilter(authFilter)
                .addFilterAfter(new CheckingAuthenticationFilter(loginMatcher), SessionManagementFilter.class);
    }

    private DaoAuthenticationProvider daoAuthProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

}
