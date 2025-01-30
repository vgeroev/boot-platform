package org.vmalibu.module.security.service.systemadminrole;

import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vmalibu.module.security.access.struct.AbstractPrivilege;
import org.vmalibu.module.security.database.dao.AccessRoleDAO;
import org.vmalibu.module.security.database.domainobject.DBAccessRole;
import org.vmalibu.module.security.database.domainobject.DBPrivilege;
import org.vmalibu.module.security.service.accessrole.AccessRoleDTO;
import org.vmalibu.module.security.service.privilege.PrivilegeGetter;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class SystemAdminRoleServiceImpl implements SystemAdminRoleService {

    public static final String SYSTEM_ADMIN_NAME = "System administrator";

    private final AccessRoleDAO accessRoleDAO;
    private final PrivilegeGetter privilegeGetter;

    @Override
    @Transactional(rollbackFor = PlatformException.class)
    public @NonNull AccessRoleDTO findRole() throws PlatformException {
        return accessRoleDAO.findAdmin()
                .map(AccessRoleDTO::from)
                .orElseThrow(
                        () -> GeneralExceptionFactory.buildNotFoundDomainObjectException(DBAccessRole.class, DBAccessRole.Fields.admin, true)
                );
    }

    @Override
    @Transactional
    public void createOrUpdateAdminRole() {
        DBAccessRole admin = accessRoleDAO.findAdmin().orElse(null);
        if (admin == null) {
            admin = new DBAccessRole();
            admin.setAdmin(true);
            admin.setName(SYSTEM_ADMIN_NAME);
        }

        Map<String, AbstractPrivilege> availablePrivileges = privilegeGetter.getAvailablePrivileges();
        Set<DBPrivilege> dbPrivileges = new HashSet<>(availablePrivileges.size());
        for (Map.Entry<String, AbstractPrivilege> entry : availablePrivileges.entrySet()) {
            dbPrivileges.add(new DBPrivilege(entry.getKey(), entry.getValue().getAccessOps()));
        }

        admin.setPrivileges(dbPrivileges);
        accessRoleDAO.save(admin);
    }
}
