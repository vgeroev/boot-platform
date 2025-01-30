package org.vmalibu.module.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.module.ModuleConfig;

import java.util.Set;

public class SecurityConfig extends ModuleConfig {

    private SecurityConfig(Builder builder) {
        super(builder);
    }

    public static class Builder extends ModuleConfig.Builder<Builder> {

        public Builder(@NonNull String uuid, @NonNull Set<String> dependencies) {
            super(uuid, dependencies);
        }

        @Override
        public @NonNull SecurityConfig build() {
            return new SecurityConfig(this);
        }
    }
}
