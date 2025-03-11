package org.vmalibu.module.security.configuration.anonymous;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AnonymousSecurityConfiguration {

    @Bean
    public SecurityFilterChain anonFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/anon/**", "/actuator/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable);

        return http.build();
    }

}
