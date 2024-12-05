package org.vmalibu.module.security.controller.anon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.vmalibu.module.security.service.user.UserDTO;
import org.vmalibu.module.security.service.user.UserService;
import org.vmalibu.modules.PlatformConsts;
import org.vmalibu.modules.module.exception.GeneralExceptionFactory;
import org.vmalibu.modules.module.exception.PlatformException;

@RestController
@RequestMapping(PlatformConsts.REST_ANON_PREFIX)
@AllArgsConstructor
public class RegisterController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
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

        @JsonProperty(JSON_USERNAME)
        private String username;

        @JsonProperty(JSON_PASSWORD)
        private String password;

    }

}
