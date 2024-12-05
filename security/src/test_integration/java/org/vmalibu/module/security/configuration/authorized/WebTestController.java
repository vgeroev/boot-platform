package org.vmalibu.module.security.configuration.authorized;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vmalibu.module.security.authorization.source.UserSource;

import static org.vmalibu.module.security.configuration.authorized.AuthorizedSecurityConfigurationTest.AUTHORIZED_CONTROLLER_PATH;

@RestController
public class WebTestController {

    @GetMapping(value = AUTHORIZED_CONTROLLER_PATH)
    @ResponseStatus(HttpStatus.OK)
    public UserSource getResult(UserSource userSource) {
        return userSource;
    }
}
