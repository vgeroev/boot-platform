package org.vmalibu.module.exercises;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.module.AbstractModule;
import org.vmalibu.modules.module.ModuleConfig;

import java.util.Set;

public class ExercisesConfig extends ModuleConfig {

    private ExercisesConfig(ExercisesConfig.Builder builder) {
        super(builder);
    }

    public static class Builder extends ModuleConfig.Builder<ExercisesConfig.Builder> {

        public Builder(@NonNull String uuid, @NonNull Set<Class<? extends AbstractModule<?>>> dependencies) {
            super(uuid, dependencies);
        }

        @Override
        public @NonNull ExercisesConfig build() {
            return new ExercisesConfig(this);
        }
    }

}
