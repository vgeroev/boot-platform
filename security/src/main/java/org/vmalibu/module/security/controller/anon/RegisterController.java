package org.vmalibu.module.security.controller.anon;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.vmalibu.module.security.SecurityModuleConsts;
import org.vmalibu.module.security.service.user.UserDTO;
import org.vmalibu.module.security.service.user.UserService;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

@RestController
@RequestMapping(SecurityModuleConsts.REST_ANON_PREFIX)
@Tag(name = "User registration", description = "Endpoints for user registration")
@AllArgsConstructor
public class RegisterController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with a username and password.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User successfully registered",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Invalid input (empty username or password)",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO register(@RequestBody RegisterRequest request) throws PlatformException {
        if (!StringUtils.hasText(request.username)) {
            throw GeneralExceptionFactory.buildEmptyValueException(RegisterRequest.JSON_USERNAME);
        }

        if (!StringUtils.hasText(request.password)) {
            throw GeneralExceptionFactory.buildEmptyValueException(RegisterRequest.JSON_PASSWORD);
        }

        return userService.create(request.username.trim(), passwordEncoder.encode(request.password.trim()));
    }

    @Data
    public static class RegisterRequest {

        static final String JSON_USERNAME = "username";
        static final String JSON_PASSWORD = "password";

        @Schema(description = "username of user", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty(JSON_USERNAME)
        private String username;

        @Schema(description = "password of user", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty(JSON_PASSWORD)
        private String password;

    }

}
