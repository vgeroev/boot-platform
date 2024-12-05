package org.vmalibu.module.security.authorization.source.builder;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.vmalibu.module.security.authorization.source.AppUserSource;
import org.vmalibu.module.security.authorization.source.UserSource;

public class AppUserSourceBuilder implements UserSourceBuilder {

    @Override
    public boolean supports(@NonNull Authentication authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication.getClass());
    }

    @Override
    public @NonNull UserSource build(@NonNull Authentication authentication) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        return (AppUserSource) token.getPrincipal();
    }
}
