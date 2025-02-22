package org.vmalibu.module.core;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.module.ModuleConfig;

import java.util.Set;

public class CoreConfig extends ModuleConfig {

    private CoreConfig(CoreConfig.Builder builder) {
        super(builder);
    }

    public static class Builder extends ModuleConfig.Builder<CoreConfig.Builder> {

        public Builder(@NonNull String uuid, @NonNull Set<String> dependencies) {
            super(uuid, dependencies);
        }

        @Override
        public @NonNull CoreConfig build() {
            return new CoreConfig(this);
        }
    }
}
