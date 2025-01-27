package org.vmalibu.module.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

public class WebMvcTestConfigurationTest {

    @Bean
    SecurityFilterChain sessionBasedFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/**")
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

}
