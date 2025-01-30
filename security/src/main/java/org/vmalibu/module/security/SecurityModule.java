package org.vmalibu.module.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.vmalibu.module.security.service.systemadminrole.SystemAdminRoleService;
import org.vmalibu.modules.module.AbstractModule;

@Component
@PropertySource(
        value = "classpath:boot-properties/security.application.properties"
)
public class SecurityModule extends AbstractModule<SecurityConfig> {

    private final SystemAdminRoleService systemAdminRoleService;

    @Autowired
    public SecurityModule(SystemAdminRoleService systemAdminRoleService) {
        super(buildConfig());
        this.systemAdminRoleService = systemAdminRoleService;
    }

    @Override
    public void onStart() {
        systemAdminRoleService.createOrUpdateAdminRole();
    }

    private static SecurityConfig buildConfig() {
        return new SecurityConfig.Builder(SecurityModuleConsts.UUID, SecurityModuleConsts.DEPENDENCIES)
                .build();
    }

}
