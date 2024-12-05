package org.vmalibu.module.security.utils;

import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.security.oauth2.jwt.Jwt;

@UtilityClass
public class JwtUtils {

    private static final String CLAIMS_USERNAME = "preferred_username";

    public @Nullable String retrieveUsername(@NonNull Jwt jwt) {
        return jwt.getClaims().get(CLAIMS_USERNAME).toString();
    }

}
