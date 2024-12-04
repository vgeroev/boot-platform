package org.vmalibu.module.security.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vmalibu.module.security.configuration.anonymous.AnonymousSecurityConfiguration;
import org.vmalibu.module.security.configuration.authorized.AuthorizedSecurityConfiguration;

@EnableWebSecurity
@Import({
        AnonymousSecurityConfiguration.class,
        AuthorizedSecurityConfiguration.class
})
@Configuration
public class SecurityConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

