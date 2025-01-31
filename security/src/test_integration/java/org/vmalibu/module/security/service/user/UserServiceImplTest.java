package org.vmalibu.module.security.service.user;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vmalibu.module.security.BaseTestClass;
import org.vmalibu.module.security.access.AccessRolePrivilege;
import org.vmalibu.module.security.access.UserPrivilege;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.database.dao.AccessRoleDAO;
import org.vmalibu.module.security.database.dao.UserDAO;
import org.vmalibu.module.security.database.domainobject.DBAccessRole;
import org.vmalibu.module.security.database.domainobject.DBUser;
import org.vmalibu.module.security.service.accessrole.AccessRoleDTO;
import org.vmalibu.module.security.service.accessrole.AccessRoleService;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;
import org.vmalibu.modules.utils.OptionalField;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import static org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils.*;

public class UserServiceImplTest extends BaseTestClass {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AccessRoleService accessRoleService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private AccessRoleDAO accessRoleDAO;

    @Test
    @DisplayName("Test Case: User creation")
    void createTest() throws PlatformException {
        String username = randomAlphanumeric(10);
        String password = randomAlphanumeric(10);

        //--------------------------------------------------------------------------------------------------------------

        UserDTO userDTO = userService.create(username, password);

        //--------------------------------------------------------------------------------------------------------------

        Assertions.assertThat(userDTO).isNotNull()
                .returns(username, UserDTO::username)
                .returns(password, UserDTO::password);
    }

    @Test
    @DisplayName("Test Case: User creation with invalid fields. Awaiting PlatformException")
    void createWithInvalidFieldsTest() {
        String username = randomAlphanumeric(10);
        String password = randomAlphanumeric(10);

        //--------------------------------------------------------------------------------------------------------------

        Assertions.assertThatThrownBy(() -> userService.create("", password))
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.EMPTY_VALUE_CODE);

        Assertions.assertThatThrownBy(() -> userService.create("    ", password))
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.EMPTY_VALUE_CODE);

        //--------------------------------------------------------------------------------------------------------------

        Assertions.assertThatThrownBy(() -> userService.create(username, ""))
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.EMPTY_VALUE_CODE);

        Assertions.assertThatThrownBy(() -> userService.create(username, "    "))
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.EMPTY_VALUE_CODE);
    }

    @Test
    @DisplayName("Test Case: User creation with not unique username. Awaiting PlatformException")
    void createWithNotUniqueUsernameTest() throws PlatformException {
        String username = randomAlphanumeric(10);

        //--------------------------------------------------------------------------------------------------------------

        userService.create(username, randomAlphanumeric(10));
        Assertions.assertThatThrownBy(() -> userService.create(username, randomAlphanumeric(10)))
                        .isExactlyInstanceOf(PlatformException.class)
                                .hasMessageContaining(GeneralExceptionFactory.NOT_UNIQUE_DOMAIN_OBJECT_CODE);
    }

    @Test
    @DisplayName("Test Case: Find user by id")
    void findUserByIdTest() throws PlatformException {
        long badId = 1234L;

        UserDTO userDTO = userService.create(randomAlphanumeric(10), randomAlphanumeric(10));

        Assertions.assertThat(userService.findById(badId)).isNull();

        UserDTO found = userService.findById(userDTO.id());
        Assertions.assertThat(found).isNotNull()
                .returns(userDTO.id(), UserDTO::id);
    }

    @Test
    @DisplayName("Test Case: Find user by username")
    void findUserByUsernameTest() throws PlatformException {
        String badUsername = "bad_username";
        String username = "username";

        UserDTO userDTO = userService.create(username, randomAlphanumeric(10));

        //--------------------------------------------------------------------------------------------------------------

        Assertions.assertThat(userService.findByUsername(badUsername)).isNull();

        UserDTO found = userService.findByUsername(username);
        Assertions.assertThat(found).isNotNull()
                .returns(userDTO.id(), UserDTO::id)
                .returns(userDTO.username(), UserDTO::username);
    }

    @Test
    @DisplayName("Test Case: Find user with privileges")
    void findWithPrivilegesTest() throws PlatformException {
        String username = "username";

        UserDTO userDTO = userService.create(username, randomAlphanumeric(10));
        AccessRoleDTO accessRole1 = accessRoleService.create("ar_1");
        accessRoleService.update(
                accessRole1.id(),
                OptionalField.empty(),
                OptionalField.of(
                        Map.of(
                                UserPrivilege.INSTANCE.getKey(), Set.of(AccessOp.WRITE),
                                AccessRolePrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ)
                        )
                )
        );
        AccessRoleDTO accessRole2 = accessRoleService.create("ar_2");
        accessRoleService.update(
                accessRole2.id(),
                OptionalField.empty(),
                OptionalField.of(
                        Map.of(
                                UserPrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ, AccessOp.DELETE)
                        )
                )
        );

        userService.addAccessRole(userDTO.id(), accessRole1.id());
        userService.addAccessRole(userDTO.id(), accessRole2.id());

        //--------------------------------------------------------------------------------------------------------------

        UserWithPrivilegesDTO user = userService.findWithPrivileges(username);
        Assertions.assertThat(user).isNotNull();

        UserDTO found = user.user();
        Assertions.assertThat(found).isNotNull()
                .returns(userDTO.id(), UserDTO::id)
                .returns(userDTO.username(), UserDTO::username);

        Assertions.assertThat(user.privileges()).isNotNull()
                .containsOnly(
                        new AbstractMap.SimpleEntry<>(UserPrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ, AccessOp.WRITE, AccessOp.DELETE)),
                        new AbstractMap.SimpleEntry<>(AccessRolePrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ))
                );
    }

    @Test
    @DisplayName("Test Case: Adding access role to user when there is no such user. Awaiting PlatformException")
    void addAccessRoleWhenThereIsNoSuchUserTest() throws PlatformException {
        AccessRoleDTO accessRole = accessRoleService.create("ar_1");
        Assertions.assertThatThrownBy(() -> userService.addAccessRole(1234L, accessRole.id()))
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.NOT_FOUND_DOMAIN_OBJECT_CODE);
    }

    @Test
    @DisplayName("Test Case: Adding access role to user when there is no such access role. Awaiting PlatformException")
    void addAccessRoleWhenThereIsNoSuchAccessRoleTest() throws PlatformException {
        UserDTO userDTO = userService.create("username", randomAlphanumeric(10));
        Assertions.assertThatThrownBy(() -> userService.addAccessRole(userDTO.id(), 1234L))
                .isExactlyInstanceOf(PlatformException.class)
                .hasMessageContaining(GeneralExceptionFactory.NOT_FOUND_DOMAIN_OBJECT_CODE);
    }

    @Test
    @DisplayName("Test Case: Adding access role to user")
    void addAccessRoleToUserTest() throws PlatformException {
        UserDTO userDTO = userService.create("username", randomAlphanumeric(10));
        AccessRoleDTO accessRole = accessRoleService.create("ar_1");

        userService.addAccessRole(userDTO.id(), accessRole.id());
        DBUser savedUser = userDAO.findWithAccessRoles(userDTO.id()).orElse(null);
        Assertions.assertThat(savedUser).isNotNull()
                .extracting(DBUser::getAccessRoles)
                .asInstanceOf(InstanceOfAssertFactories.COLLECTION)
                .anySatisfy(
                        ar -> Assertions.assertThat((DBAccessRole) ar)
                                .returns(accessRole.id(), DBAccessRole::getId)
                );
    }

}
