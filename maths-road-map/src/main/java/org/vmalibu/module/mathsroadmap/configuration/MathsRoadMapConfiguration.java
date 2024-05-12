package org.vmalibu.module.mathsroadmap.configuration;

import com.antkorwin.xsync.XSync;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MathsRoadMapConfiguration {

    @Bean(name = "articlePreviewXSync")
    public XSync<String> articlePreviewXSync() {
        return new XSync<>();
    }
}
