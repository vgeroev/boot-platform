package org.vmalibu.module.mathsroadmap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.modules.module.AbstractModule;
import org.vmalibu.modules.module.ModuleConfig;

import java.util.Set;

public class MathsRoadMapConfig extends ModuleConfig {

    private MathsRoadMapConfig(MathsRoadMapConfig.Builder builder) {
        super(builder);
    }

    public static class Builder extends ModuleConfig.Builder<MathsRoadMapConfig.Builder> {

        public Builder(@NonNull String uuid, @NonNull Set<Class<? extends AbstractModule<?>>> dependencies) {
            super(uuid, dependencies);
        }

        @Override
        public @NonNull MathsRoadMapConfig build() {
            return new MathsRoadMapConfig(this);
        }
    }
}
