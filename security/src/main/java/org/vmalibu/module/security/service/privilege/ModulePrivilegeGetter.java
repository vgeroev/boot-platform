package org.vmalibu.module.security.service.privilege;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.module.security.access.struct.AbstractPrivilege;
import org.vmalibu.modules.module.AbstractModule;

import java.util.List;

public interface ModulePrivilegeGetter {

    @NonNull List<AbstractPrivilege> getPrivileges();

    @NonNull AbstractModule<?> getModule();

}
