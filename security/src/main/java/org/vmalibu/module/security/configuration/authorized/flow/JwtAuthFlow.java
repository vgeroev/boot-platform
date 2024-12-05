package org.vmalibu.module.security.configuration.authorized.flow;

import jakarta.servlet.http.HttpServletRequest;
import org.checkerframework.checker.nullness.qual.NonNull;

public class JwtAuthFlow implements AuthFlow {

    static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public boolean supports(@NonNull HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        return header != null && header.startsWith("Bearer ");
    }
}
