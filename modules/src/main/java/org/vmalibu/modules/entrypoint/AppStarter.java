package org.vmalibu.modules.entrypoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = "org.vmalibu"
)
@EntityScan(
        basePackages = "org.vmalibu"
)
@EnableJpaRepositories(
        basePackages = "org.vmalibu"
)
@EnableJpaAuditing
public class AppStarter {

    public static void main(String[] args) {
        SpringApplication.run(AppStarter.class, args);
    }
}
