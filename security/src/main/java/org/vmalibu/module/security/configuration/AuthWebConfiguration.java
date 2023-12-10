package org.vmalibu.module.security.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.vmalibu.module.security.argumentresolver.UserSourceHandlerMethodArgumentResolver;
import org.vmalibu.modules.web.configuration.MainWebConfiguration;

import java.util.List;

@Configuration
@Import({ MainWebConfiguration.class })
public class AuthWebConfiguration implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserSourceHandlerMethodArgumentResolver());
    }
}

