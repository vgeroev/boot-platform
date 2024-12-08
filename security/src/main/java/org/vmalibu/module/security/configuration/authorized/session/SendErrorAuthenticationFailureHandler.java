package org.vmalibu.module.security.configuration.authorized.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@Slf4j
public class SendErrorAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        // TODO Not working
//        request.setAttribute(DefaultErrorAttributes.class.getName() + ".ERROR",
//                new IllegalStateException("Authentication failed. Maybe you are logged in too many times"));
        log.warn("Session authentication failed", exception);
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authentication failed. Maybe you are logged in too many times");
    }
}
