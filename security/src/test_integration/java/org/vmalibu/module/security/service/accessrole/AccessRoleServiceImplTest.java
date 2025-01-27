package org.vmalibu.module.security.service.accessrole;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vmalibu.module.security.BaseTestClass;
import org.vmalibu.module.security.access.AccessRolePrivilege;
import org.vmalibu.module.security.access.UserPrivilege;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.exception.SecurityExceptionFactory;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.Map;
import java.util.Set;

import static org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class AccessRoleServiceImplTest extends BaseTestClass {

    @Autowired
    private AccessRoleServiceImpl accessRoleService;

    @Test
    @DisplayName("Test Case: AccessRole creation")
    void createTest() throws PlatformException {
        String name = randomAlphanumeric(10);

        //--------------------------------------------------------------------------------------------------------------

        AccessRoleDTO accessRoleDTO = accessRoleService.create(name);

        //--------------------------------------------------------------------------------------------------------------

        Assertions.assertThat(accessRoleDTO).isNotNull()
                .returns(name, AccessRoleDTO::name);
        Assertions.assertThat(accessRoleDTO.privileges()).isEmpty();
    }

    @Test
    @DisplayName("Test Case: AccessRole creation when name is empty")
    void createWhenNameIsEmptyTest() {
        Assertions.assertThatThrownBy(() -> accessRoleService.create(""))
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.EMPTY_VALUE_CODE);

        Assertions.assertThatThrownBy(() -> accessRoleService.create("   "))
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.EMPTY_VALUE_CODE);
    }

    @Test
    @DisplayName("Test Case: AccessRole creation when name is not unique")
    void createWhenNameIsNotUniqueTest() throws PlatformException {
        String name = randomAlphanumeric(10);

        //--------------------------------------------------------------------------------------------------------------

       accessRoleService.create(name);

        //--------------------------------------------------------------------------------------------------------------

        Assertions.assertThatThrownBy(() -> accessRoleService.create(name))
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.NOT_UNIQUE_DOMAIN_OBJECT_CODE);
    }

    @Test
    @DisplayName("Test Case: AccessRole removing")
    void removeAccessRoleTest() throws PlatformException {
        String name = randomAlphanumeric(10);

        //--------------------------------------------------------------------------------------------------------------

        AccessRoleDTO accessRoleDTO = accessRoleService.create(name);
        accessRoleService.update(
                accessRoleDTO.id(),
                OptionalField.empty(),
                OptionalField.of(
                        Map.of(
                                UserPrivilege.INSTANCE.getKey(), Set.of(AccessOp.WRITE),
                                AccessRolePrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ))
                )
        );
        accessRoleService.remove(accessRoleDTO.id());

        //--------------------------------------------------------------------------------------------------------------

        Assertions.assertThat(accessRoleService.findById(accessRoleDTO.id())).isNull();
    }

    @Test
    @DisplayName("Test Case: AccessRole updating")
    void updateTest() throws PlatformException {
        String name = randomAlphanumeric(10);
        AccessRoleDTO accessRole = accessRoleService.create(name);

        //--------------------------------------------------------------------------------------------------------------

        AccessRoleDTO updated = accessRoleService.update(
                accessRole.id(),
                OptionalField.empty(),
                OptionalField.of(
                        Map.of(
                                UserPrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ, AccessOp.WRITE),
                                AccessRolePrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ))
                )
        );

        Assertions.assertThat(updated).isNotNull()
                .returns(name, AccessRoleDTO::name);
        Assertions.assertThat(updated.privileges()).isNotNull()
                .containsOnly(
                        new PrivilegeDTO(UserPrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ, AccessOp.WRITE)),
                        new PrivilegeDTO(AccessRolePrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ))
                );

        AccessRoleDTO persisted = accessRoleService.findById(accessRole.id());

        Assertions.assertThat(persisted).isNotNull();
        Assertions.assertThat(persisted).isNotNull()
                .returns(name, AccessRoleDTO::name);
        Assertions.assertThat(persisted.privileges()).isNotNull()
                .containsOnly(
                        new PrivilegeDTO(UserPrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ, AccessOp.WRITE)),
                        new PrivilegeDTO(AccessRolePrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ))
                );

        //--------------------------------------------------------------------------------------------------------------


        String newName = randomAlphanumeric(10);
        updated = accessRoleService.update(
                accessRole.id(),
                OptionalField.of(newName),
                OptionalField.empty()
        );

        Assertions.assertThat(updated).isNotNull()
                .returns(newName, AccessRoleDTO::name);
        Assertions.assertThat(updated.privileges()).isNotNull()
                .containsOnly(
                        new PrivilegeDTO(UserPrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ, AccessOp.WRITE)),
                        new PrivilegeDTO(AccessRolePrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ))
                );

        persisted = accessRoleService.findById(accessRole.id());

        Assertions.assertThat(persisted).isNotNull();
        Assertions.assertThat(persisted).isNotNull()
                .returns(newName, AccessRoleDTO::name);
        Assertions.assertThat(persisted.privileges()).isNotNull()
                .containsOnly(
                        new PrivilegeDTO(UserPrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ, AccessOp.WRITE)),
                        new PrivilegeDTO(AccessRolePrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ))
                );
    }

    @Test
    @DisplayName("Test Case: AccessRole updating when privilege key is invalid. Awaiting PlatformException")
    void updateWhenPrivilegeKeyIsInvalidTest() throws PlatformException {
        String name = randomAlphanumeric(10);
        AccessRoleDTO accessRole = accessRoleService.create(name);

        //--------------------------------------------------------------------------------------------------------------

        String invalidPrivilegeKey = "invalid_key";
        Assertions.assertThatThrownBy(() -> accessRoleService.update(
                accessRole.id(),
                OptionalField.empty(),
                OptionalField.of(
                        Map.of(
                                UserPrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ, AccessOp.WRITE),
                                invalidPrivilegeKey, Set.of(AccessOp.READ))
                )
        )).isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(SecurityExceptionFactory.INVALID_PRIVILEGE_KEY)
                .hasMessageContaining(invalidPrivilegeKey);
    }

    @Test
    @DisplayName("Test Case: AccessRole updating when privilege has invalid access operation. Awaiting PlatformException")
    void updateWhenPrivilegeHasInvalidAccessOperationTest() throws PlatformException {
        String name = randomAlphanumeric(10);
        AccessRoleDTO accessRole = accessRoleService.create(name);

        //--------------------------------------------------------------------------------------------------------------

        String invalidPrivilegeKey = "invalid_key";
        Assertions.assertThatThrownBy(() -> accessRoleService.update(
                        accessRole.id(),
                        OptionalField.empty(),
                        OptionalField.of(
                                Map.of(
                                        UserPrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ, AccessOp.WRITE),
                                        // EXECUTE does not appropriate to AccessRolePrivilege
                                        AccessRolePrivilege.INSTANCE.getKey(), Set.of(AccessOp.EXECUTE))
                        )
                )).isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(SecurityExceptionFactory.INVALID_PRIVILEGE_ACCESS_OPS);
    }

}
