package org.vmalibu.module.security.service.privilege;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;
import org.vmalibu.module.security.SecurityModuleConsts;
import org.vmalibu.module.security.access.AccessRolePrivilege;
import org.vmalibu.module.security.access.UserPrivilege;
import org.vmalibu.module.security.access.struct.AbstractPrivilege;

import java.util.List;
import java.util.Set;

@Component
public class SecurityPrivilegeGetter implements ModulePrivilegeGetter {

    @Override
    public @NonNull List<AbstractPrivilege> getPrivileges() {
        return List.of(
                UserPrivilege.INSTANCE,
                AccessRolePrivilege.INSTANCE
        );
    }

    @Override
    public @NonNull String getModuleUUID() {
        return SecurityModuleConsts.UUID;
    }

    @Override
    public @NonNull Set<@NonNull String> getModuleDependencies() {
        return SecurityModuleConsts.DEPENDENCIES;
    }
}
