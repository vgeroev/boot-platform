package org.vmalibu.module.security;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.vmalibu.modules.module.AbstractModule;

import java.util.Set;

@Component
@PropertySource(
        value = "classpath:boot-properties/security.application.properties"
)
public class SecurityModule extends AbstractModule<SecurityConfig> {

    public SecurityModule() {
        super(buildConfig());
    }

    private static SecurityConfig buildConfig() {
        return new SecurityConfig.Builder(SecurityModuleConsts.UUID, Set.of())
                .build();
    }

}
