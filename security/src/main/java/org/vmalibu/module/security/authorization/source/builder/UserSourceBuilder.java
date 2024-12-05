package org.vmalibu.module.security.authorization.source.builder;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.core.Authentication;
import org.vmalibu.module.security.authorization.source.UserSource;

public interface UserSourceBuilder {

    boolean supports(@NonNull Authentication authentication);

    @NonNull UserSource build(@NonNull Authentication authentication);
}
