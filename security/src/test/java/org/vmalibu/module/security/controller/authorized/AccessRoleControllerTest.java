package org.vmalibu.module.security.controller.authorized;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.vmalibu.module.security.SecurityModuleConsts;
import org.vmalibu.module.security.WebMvcTestConfigurationTest;
import org.vmalibu.module.security.access.AccessRolePrivilege;
import org.vmalibu.module.security.access.UserPrivilege;
import org.vmalibu.module.security.access.struct.AccessOp;
import org.vmalibu.module.security.configuration.AuthWebConfiguration;
import org.vmalibu.module.security.service.accessrole.AccessRoleDTO;
import org.vmalibu.module.security.service.accessrole.AccessRoleService;
import org.vmalibu.module.security.service.accessrole.PrivilegeDTO;
import org.vmalibu.modules.utils.OptionalField;
import org.vmalibu.modules.web.advice.ExceptionControllerAdvice;
import org.vmalibu.modules.web.advice.JsonResponseBodyAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccessRoleController.class)
@ContextConfiguration(classes = {
        AccessRoleController.class,
        AuthWebConfiguration.class,
        WebMvcTestConfigurationTest.class,
        ExceptionControllerAdvice.class,
        JsonResponseBodyAdvice.class
})
class AccessRoleControllerTest {

    @Autowired
    private MockMvc rest;

    @MockBean
    private AccessRoleService accessRoleService;

    @Test
    @DisplayName(
            "Test Case: Invoking REST controller to create access role. Awaiting 201 and creating access role"
    )
    void createAccessRoleTest() throws Exception {
        String name = RandomStringUtils.randomAlphabetic(10);

        AccessRoleDTO accessRole = new AccessRoleDTO(1234L, name, List.of());
        Mockito.when(accessRoleService.create(name)).thenReturn(accessRole);

        AccessRoleController.CreateAccessRoleRequest request = new AccessRoleController.CreateAccessRoleRequest();
        request.setName(name);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(request);

        rest.perform(
                post(convertRelUrl("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(accessRole.id()))
                .andExpect(jsonPath("$.data.name").value(accessRole.name()));

        Mockito.verify(accessRoleService, Mockito.only()).create(name);
    }

    @Test
    @DisplayName(
            "Test Case: Invoking REST controller to create access role when name is empty. Awaiting 409"
    )
    void createAccessRoleWhenNameIsEmptyTest() throws Exception {
        String name = "";

        AccessRoleController.CreateAccessRoleRequest request = new AccessRoleController.CreateAccessRoleRequest();
        request.setName(name);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(request);

        rest.perform(
                        post(convertRelUrl("/create"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                ).andExpect(status().is4xxClientError());

        //--------------------------------------------------------------------------------------------------------------

        name = "    ";

        request = new AccessRoleController.CreateAccessRoleRequest();
        request.setName(name);
        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        requestJson = objectWriter.writeValueAsString(request);

        rest.perform(
                post(convertRelUrl("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpect(status().isConflict());

        Mockito.verify(accessRoleService, Mockito.never()).create(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName(
            "Test Case: Invoking REST controller to update access role. Awaiting 200 and updating access role"
    )
    void updateAccessRoleTest() throws Exception {
        long id = ThreadLocalRandom.current().nextLong();
        String name = RandomStringUtils.randomAlphabetic(10);
        Map<String, Set<AccessOp>> privileges = new HashMap<>();
        privileges.put(UserPrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ));
        privileges.put(AccessRolePrivilege.INSTANCE.getKey(), Set.of(AccessOp.WRITE));

        AccessRoleDTO accessRole = new AccessRoleDTO(
                id,
                name,
                List.of(
                        new PrivilegeDTO(UserPrivilege.INSTANCE.getKey(), Set.of(AccessOp.READ)),
                        new PrivilegeDTO(AccessRolePrivilege.INSTANCE.getKey(), Set.of(AccessOp.WRITE))
                )
        );
        Mockito.when(accessRoleService.update(ArgumentMatchers.eq(id), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(accessRole);

        Map<String, Object> mapRequest = new HashMap<>();
        mapRequest.put(AccessRoleController.UpdateAccessRoleRequest.JSON_NAME, name);
        mapRequest.put(AccessRoleController.UpdateAccessRoleRequest.JSON_PRIVILEGES, privileges);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(mapRequest);

        rest.perform(
                        patch(convertRelUrl("/update/%d".formatted(id)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                ).andExpect(status().isOk());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<OptionalField<String>> nameCaptor = ArgumentCaptor.forClass(OptionalField.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<OptionalField<Map<String, Set<AccessOp>>>> privilegesCaptor = ArgumentCaptor.forClass(OptionalField.class);
        Mockito.verify(accessRoleService, Mockito.only()).update(
                ArgumentMatchers.eq(id),
                nameCaptor.capture(),
                privilegesCaptor.capture()
        );

        Assertions.assertThat(nameCaptor.getValue().get()).isEqualTo(name);
        Assertions.assertThat(privilegesCaptor.getValue().get()).isEqualTo(privileges);
    }

    private String convertRelUrl(String relURL) {
        return SecurityModuleConsts.REST_AUTHORIZED_PREFIX + "/access-role" + relURL;
    }
}
