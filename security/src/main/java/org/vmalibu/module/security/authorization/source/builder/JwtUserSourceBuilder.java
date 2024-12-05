package org.vmalibu.module.security.authorization.source.builder;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.core.Authentication;
import org.vmalibu.module.security.authentication.jwt.JwtAuthenticationTokenExt;
import org.vmalibu.module.security.authorization.source.JwtUserSource;
import org.vmalibu.module.security.authorization.source.UserSource;

public class JwtUserSourceBuilder implements UserSourceBuilder {

    @Override
    public boolean supports(@NonNull Authentication authentication) {
        return JwtAuthenticationTokenExt.class.equals(authentication.getClass());
    }

    @Override
    public @NonNull UserSource build(@NonNull Authentication authentication) {
        JwtAuthenticationTokenExt token = (JwtAuthenticationTokenExt) authentication;
        return new JwtUserSource(token.getUserId(), token.getUsername(), token.getName());
    }
}
