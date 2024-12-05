package org.vmalibu.module.security;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.vmalibu.module.security.authentication.jwt.JwtAuthenticationManager;
import org.vmalibu.module.security.configuration.AuthWebConfiguration;
import org.vmalibu.module.security.configuration.SecurityConfiguration;
import org.vmalibu.module.security.service.privilege.PrivilegeGetter;
import org.vmalibu.modules.web.exception.advice.ExceptionControllerAdvice;

@ContextConfiguration(classes = {
        AuthWebConfiguration.class,
        ExceptionControllerAdvice.class,
        SecurityConfiguration.class,
        JwtAuthenticationManager.class,
        PrivilegeGetter.class
})
public class WebMvcTestBaseClass {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("jwkSetUri", () ->"http://stub");
    }
}
