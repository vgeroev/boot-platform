package org.vmalibu.module.security.service.systemadminrole;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.vmalibu.module.security.BaseTestClass;
import org.vmalibu.module.security.access.struct.AbstractPrivilege;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.database.dao.AccessRoleDAO;
import org.vmalibu.module.security.database.domainobject.DBAccessRole;
import org.vmalibu.module.security.service.accessrole.AccessRoleDTO;
import org.vmalibu.module.security.service.privilege.PrivilegeGetter;
import org.vmalibu.modules.module.exception.PlatformException;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Transactional
public class SystemAdminRoleServiceImplTest extends BaseTestClass {

    @Autowired
    private SystemAdminRoleServiceImpl systemAdminRoleService;

    @Autowired
    private AccessRoleDAO accessRoleDAO;

    @Autowired
    private PrivilegeGetter privilegeGetter;

    @Test
    @DisplayName("Test Case: Trying to find admin access role when this role exists")
    void findRoleTest() throws PlatformException {
        AccessRoleDTO adminRoleDTO = systemAdminRoleService.findRole();
        Assertions.assertThat(adminRoleDTO).isNotNull()
                .returns(true, AccessRoleDTO::admin)
                .returns(SystemAdminRoleServiceImpl.SYSTEM_ADMIN_NAME, AccessRoleDTO::name);
    }

    @Test
    @DisplayName("Test Case: Creating or updating admin role")
    void createOrUpdateAdminRoleTest() {
        Runnable checker = () -> {
            Optional<DBAccessRole> oAdmin = accessRoleDAO.findAdmin();
            Assertions.assertThat(oAdmin).isPresent();
            DBAccessRole admin = oAdmin.get();
            Assertions.assertThat(admin)
                    .returns(true, DBAccessRole::isAdmin)
                    .returns(SystemAdminRoleServiceImpl.SYSTEM_ADMIN_NAME, DBAccessRole::getName);

            Set<Map.Entry<String, AbstractPrivilege>> privilegeEntries = privilegeGetter.getAvailablePrivileges().entrySet();
            Map<String, Set<AccessOp>> privileges = admin.getPrivileges();
            Assertions.assertThat(privileges).hasSameSizeAs(privilegeEntries);
            for (Map.Entry<String, AbstractPrivilege> entry : privilegeEntries) {
                String privilegeKey = entry.getKey();
                Set<AccessOp> accessOps = privileges.get(privilegeKey);
                Assertions.assertThat(accessOps).isNotNull().hasSameElementsAs(entry.getValue().getAccessOps());
            }
        };

        systemAdminRoleService.createOrUpdateAdminRole();
        checker.run();
        systemAdminRoleService.createOrUpdateAdminRole();
        checker.run();
    }

}
