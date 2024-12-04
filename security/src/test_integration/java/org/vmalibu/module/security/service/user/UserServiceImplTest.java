package org.vmalibu.module.security.service.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vmalibu.module.security.BaseTestClass;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

import static org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils.*;

public class UserServiceImplTest extends BaseTestClass {

    @Autowired
    private UserServiceImpl userService;

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
}
