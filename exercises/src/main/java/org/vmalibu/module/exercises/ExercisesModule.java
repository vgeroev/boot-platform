package org.vmalibu.module.exercises;

import org.springframework.stereotype.Component;
import org.vmalibu.module.security.SecurityModule;
import org.vmalibu.modules.module.AbstractModule;

import java.util.Set;

@Component
public class ExercisesModule extends AbstractModule<ExercisesConfig> {

    public ExercisesModule() {
        super(buildConfig());
    }

    private static ExercisesConfig buildConfig() {
        return new ExercisesConfig.Builder(ExercisesModuleConsts.UUID, Set.of(SecurityModule.class))
                .build();
    }
}
