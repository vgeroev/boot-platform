package org.vmalibu.module.security.configuration.authorized.flow;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SessionBasedAuthFlow implements AuthFlow {

    private final String sessionName;

    public SessionBasedAuthFlow(@NonNull String sessionName) {
        this.sessionName = sessionName;
    }

    @Override
    public boolean supports(@NonNull HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }

        for (Cookie cookie : cookies) {
            if (sessionName.equals(cookie.getName())) {
                return true;
            }
        }

        return false;
    }
}
