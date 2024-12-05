package org.vmalibu.module.security;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.vmalibu.modules.entrypoint.AppStarter;

@SpringBootTest(classes = AppStarter.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        registry.add("spring.jpa.properties.hibernate.show_sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.use_sql_comments", () -> "true");
        registry.add("spring.jpa.properties.hibernate.format_sql", () ->"true");
        registry.add("jwkSetUri", () ->"http://stub");
    }

}
