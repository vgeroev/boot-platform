package org.vmalibu.module.security.service.systemadminrole;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.vmalibu.module.security.service.accessrole.AccessRoleDTO;
import org.vmalibu.modules.module.exception.PlatformException;

public interface SystemAdminRoleService {

    @NonNull AccessRoleDTO findRole() throws PlatformException;

    void createOrUpdateAdminRole();
}
