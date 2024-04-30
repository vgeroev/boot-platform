package org.vmalibu.module.mathsroadmap;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.vmalibu.module.security.SecurityModule;
import org.vmalibu.modules.module.AbstractModule;

import java.util.Set;

@Component
@PropertySource(
        value = {
                "classpath:boot-properties/mathsroadmap.application.properties"
        }
)
public class MathsRoadMapModule extends AbstractModule<MathsRoadMapConfig> {

    public MathsRoadMapModule() {
        super(buildConfig());
    }

    private static MathsRoadMapConfig buildConfig() {
        return new MathsRoadMapConfig.Builder(MathsRoadMapConsts.UUID, Set.of(SecurityModule.class))
                .build();
    }

}
