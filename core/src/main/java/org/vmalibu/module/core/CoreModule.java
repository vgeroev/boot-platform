package org.vmalibu.module.core;

import org.springframework.stereotype.Component;
import org.vmalibu.module.security.SecurityModuleConsts;
import org.vmalibu.modules.module.AbstractModule;

import java.util.Set;

@Component
public class CoreModule extends AbstractModule<CoreConfig> {

    public CoreModule() {
        super(buildConfig());
    }

    private static CoreConfig buildConfig() {
        return new CoreConfig.Builder(CoreConsts.UUID, Set.of(SecurityModuleConsts.UUID))
                .build();
    }

}
