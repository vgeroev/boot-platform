package org.vmalibu.module.security.configuration;

import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.vmalibu.module.security.configuration.anonymous.AnonymousSecurityConfiguration;
import org.vmalibu.module.security.configuration.authorized.AuthorizedSecurityConfiguration;

@EnableWebSecurity
@Import({
        AnonymousSecurityConfiguration.class,
        AuthorizedSecurityConfiguration.class
})
public class SecurityConfiguration {

}

