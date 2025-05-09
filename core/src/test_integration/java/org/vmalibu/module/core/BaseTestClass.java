package org.vmalibu.module.core;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.vmalibu.modules.entrypoint.AppStarter;

@SpringBootTest(classes = AppStarter.class)
@Import( { PostgresConfig.class, BaseTestClass.TestConfig.class })
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
        registry.add("spring.jpa.properties.hibernate.show_sql", () -> "false");
        registry.add("spring.jpa.properties.hibernate.use_sql_comments", () -> "false");
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

}
