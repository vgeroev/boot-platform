package org.vmalibu.module.security.controller.anon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.vmalibu.module.security.WebMvcTestBaseClass;
import org.vmalibu.module.security.service.user.UserDTO;
import org.vmalibu.module.security.service.user.UserService;
import org.vmalibu.modules.PlatformConsts;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(RegisterController.class)
@ContextConfiguration(classes = RegisterController.class)
class RegisterControllerTest extends WebMvcTestBaseClass {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Test Case: Register user with correct values")
    void registerUserTest() throws Exception {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long id = random.nextLong();
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        Date createdAt = Date.from(Instant.now().truncatedTo(ChronoUnit.MILLIS));
        Date updatedAt = random.nextBoolean() ? Date.from(Instant.now().truncatedTo(ChronoUnit.MILLIS)) : null;

        UserDTO userDTO = new UserDTO(id, username, password, createdAt, updatedAt);
        when(userService.create(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(userDTO);

        when(passwordEncoder.encode(password)).thenReturn(password);

        //--------------------------------------------------------------------------------------------------------------

        RegisterController.RegisterRequest request = new RegisterController.RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(request);

        mockMvc.perform(
                post(path("/register"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));

        verify(passwordEncoder, only()).encode(password);
        verify(userService, only()).create(username, password);
    }

    @Test
    @DisplayName("Test Case: Register user with invalid values")
    void registerUserWithInvalidValuesTest() throws Exception {
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        //--------------------------------------------------------------------------------------------------------------

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();

        // username = null
        {
            RegisterController.RegisterRequest request = new RegisterController.RegisterRequest();
            request.setUsername(null);
            request.setPassword(password);

            String requestJson = objectWriter.writeValueAsString(request);

            mockMvc.perform(
                    post(path("/register"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)

            ).andExpect(status().is4xxClientError());
        }

        // username is blank
        {
            RegisterController.RegisterRequest request = new RegisterController.RegisterRequest();
            request.setUsername("  ");
            request.setPassword(password);

            String requestJson = objectWriter.writeValueAsString(request);

            mockMvc.perform(
                    post(path("/register"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)

            ).andExpect(status().is4xxClientError());
        }

        // password = null
        {
            RegisterController.RegisterRequest request = new RegisterController.RegisterRequest();
            request.setUsername(username);
            request.setPassword(null);

            String requestJson = objectWriter.writeValueAsString(request);

            mockMvc.perform(
                    post(path("/register"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)

            ).andExpect(status().is4xxClientError());
        }

        // password is blank
        {
            RegisterController.RegisterRequest request = new RegisterController.RegisterRequest();
            request.setUsername(username);
            request.setPassword("   ");

            String requestJson = objectWriter.writeValueAsString(request);

            mockMvc.perform(
                    post(path("/register"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)

            ).andExpect(status().is4xxClientError());
        }

        verify(passwordEncoder, never()).encode(ArgumentMatchers.anyString());
        verify(userService, never()).create(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    private static String path(String relPath) {
        return PlatformConsts.REST_ANON_PREFIX + relPath;
    }
}
