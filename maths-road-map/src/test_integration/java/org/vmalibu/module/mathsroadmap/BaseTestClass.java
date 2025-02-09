package org.vmalibu.module.mathsroadmap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.entrypoint.AppStarter;

import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest(classes = AppStarter.class)
@Import(PostgresConfig.class)
public class BaseTestClass {

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void cleanupDatabase() {
        databaseCleanup.execute();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
//        registry.add("spring.jpa.properties.hibernate.show_sql", () -> "true");
//        registry.add("spring.jpa.properties.hibernate.use_sql_comments", () -> "true");
//        registry.add("spring.jpa.properties.hibernate.format_sql", () ->"true");
        registry.add("logging.level.liquibase", () -> "DEBUG");
        registry.add("spring.liquibase.verbose", () -> "true");

        registry.add("jwkSetUri", () ->"http://stub");
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public DatasourceBeanPostProcessorTest datasourceBeanPostProcessorTest() {
            return new DatasourceBeanPostProcessorTest();
        }

    }

    protected UserSource getUserSource(String username) {
        return new UserSource() {

            @Override
            public long getId() {
                return ThreadLocalRandom.current().nextLong();
            }

            @Override
            public @NonNull String getUsername() {
                return username;
            }
        };
    }
}
