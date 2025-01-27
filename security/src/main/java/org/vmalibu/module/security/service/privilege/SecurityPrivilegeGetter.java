package org.vmalibu.module.security.service.privilege;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;
import org.vmalibu.module.security.SecurityModule;
import org.vmalibu.module.security.access.AccessRolePrivilege;
import org.vmalibu.module.security.access.UserPrivilege;
import org.vmalibu.module.security.access.struct.AbstractPrivilege;
import org.vmalibu.modules.module.AbstractModule;

import java.util.List;

@Component
public class SecurityPrivilegeGetter implements ModulePrivilegeGetter {

    private final SecurityModule module;

    public SecurityPrivilegeGetter(SecurityModule module) {
        this.module = module;
    }

    @Override
    public @NonNull List<AbstractPrivilege> getPrivileges() {
        return List.of(
                UserPrivilege.INSTANCE,
                AccessRolePrivilege.INSTANCE
        );
    }

    @Override
    public @NonNull AbstractModule<?> getModule() {
        return module;
    }
}
