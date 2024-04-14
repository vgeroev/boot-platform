package org.vmalibu.module.mathsroadmap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.vmalibu.modules.entrypoint.AppStarter;

@SpringBootTest(classes = AppStarter.class)
@Testcontainers
public class BaseTestClass {

    private static final String DATABASE_NAME = "test-db";

    @Autowired
    private DatabaseCleanup databaseCleanup;

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.7")
            .withDatabaseName(DATABASE_NAME);

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @BeforeAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    public void cleanupDatabase() {
        databaseCleanup.execute();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("hibernate.dialect", postgres::getJdbcUrl);
        registry.add("spring.datasource.url", () ->"jdbc:tc:postgresql:14.7:///" + DATABASE_NAME);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.properties.hibernate.show_sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.use_sql_comments", () -> "true");
        registry.add("spring.jpa.properties.hibernate.format_sql", () ->"true");
    }
}
