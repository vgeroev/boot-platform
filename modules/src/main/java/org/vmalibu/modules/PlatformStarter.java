package org.vmalibu.modules;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.vmalibu.modules.service.platformmanager.PlatformManager;
import org.vmalibu.modules.module.exception.PlatformException;

@Component
@PropertySource(
        value = {
                "classpath:boot-properties/database.application.properties",
                "classpath:boot-properties/web.application.properties",
                "classpath:boot-properties/actuator.application.properties"
        }
)
@Slf4j
@AllArgsConstructor
public class PlatformStarter {

    private PlatformManager platformManager;

    @PostConstruct
    public void init() throws PlatformException {
        platformManager.start();
    }

    @PreDestroy
    public void destroy() throws PlatformException {
        platformManager.destroy();
    }

}
