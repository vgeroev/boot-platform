package org.vmalibu.module.security.configuration.authorized.flow;

import
        jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SessionBasedAuthFlow implements AuthFlow {

    public static final String SESSION_ID = "JSESSIONID";

    @Override
    public boolean supports(@NonNull HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }

        for (Cookie cookie : cookies) {
            if (SESSION_ID.equals(cookie.getName())) {
                return true;
            }
        }

        return false;
    }
}
