package org.vmalibu.module.security.controller.authorized;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.vmalibu.module.security.SecurityModuleConsts;
import org.vmalibu.module.security.WebMvcTestConfigurationTest;
import org.vmalibu.module.security.configuration.AuthWebConfiguration;
import org.vmalibu.module.security.service.user.UserService;
import org.vmalibu.modules.web.advice.ExceptionControllerAdvice;
import org.vmalibu.modules.web.advice.JsonResponseBodyAdvice;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {
        UserController.class,
        AuthWebConfiguration.class,
        WebMvcTestConfigurationTest.class,
        ExceptionControllerAdvice.class,
        JsonResponseBodyAdvice.class
})
class UserControllerTest {

    @Autowired
    private MockMvc rest;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName(
            "Test Case: Invoking REST controller to add access role to user. Awaiting 200 and adding access role"
    )
    void addAccessRoleTest() throws Exception {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long id = random.nextLong();
        long accessRoleId = random.nextLong();

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String content = objectWriter.writeValueAsString(Set.of(accessRoleId));

        rest.perform(
                patch(convertRelUrl("/%d/add-access-role".formatted(id)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).addAccessRoles(id, Set.of(accessRoleId));
    }

    @Test
    @DisplayName(
            "Test Case: Invoking REST controller to remove access role to user." +
                    " Awaiting 200 and removing access role"
    )
    void removeAccessRoleTest() throws Exception {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long id = random.nextLong();
        long accessRoleId = random.nextLong();

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String content = objectWriter.writeValueAsString(Set.of(accessRoleId));

        rest.perform(
                patch(convertRelUrl("/%d/remove-access-role".formatted(id)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).removeAccessRoles(id, Set.of(accessRoleId));
    }

    private String convertRelUrl(String relURL) {
        return SecurityModuleConsts.REST_AUTHORIZED_PREFIX + "/user" + relURL;
    }

}
