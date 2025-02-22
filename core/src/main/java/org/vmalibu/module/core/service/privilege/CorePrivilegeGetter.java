package org.vmalibu.module.core.service.privilege;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;
import org.vmalibu.module.core.CoreConsts;
import org.vmalibu.module.core.access.TagManagingPrivilege;
import org.vmalibu.module.security.access.struct.AbstractPrivilege;
import org.vmalibu.module.security.service.privilege.ModulePrivilegeGetter;

import java.util.List;
import java.util.Set;

@Component
public class CorePrivilegeGetter implements ModulePrivilegeGetter {

    @Override
    public @NonNull List<AbstractPrivilege> getPrivileges() {
        return List.of(
                TagManagingPrivilege.INSTANCE
        );
    }

    @Override
    public @NonNull String getModuleUUID() {
        return CoreConsts.UUID;
    }

    @Override
    public @NonNull Set<@NonNull String> getModuleDependencies() {
        return CoreConsts.DEPENDENCIES;
    }
}
