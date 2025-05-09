package org.vmalibu.modules.module;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.Set;

public class ModuleConfig {

    private final String uuid;
    private final Set<String> dependencies;

    protected ModuleConfig(Builder<?> builder) {
        this.uuid = builder.uuid;
        this.dependencies = builder.dependencies;
    }

    public @NonNull String getUuid() {
        return uuid;
    }

    public @NonNull Set<String> getDependencies() {
        return dependencies;
    }

    public abstract static class Builder<T extends Builder<?>> {

        private final String uuid;
        private final Set<String> dependencies;

        protected Builder(@NonNull String uuid, @NonNull Set<String> dependencies) {
            this.uuid = Objects.requireNonNull(uuid);
            this.dependencies = Objects.requireNonNull(dependencies);
        }

        public abstract @NonNull ModuleConfig build();
    }
}
