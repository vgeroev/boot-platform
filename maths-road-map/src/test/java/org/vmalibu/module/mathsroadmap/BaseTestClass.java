package org.vmalibu.module.mathsroadmap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.vmalibu.module.security.authorization.source.UserSource;
import org.vmalibu.modules.entrypoint.AppStarter;

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
        registry.add("spring.jpa.properties.hibernate.show_sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.use_sql_comments", () -> "true");
        registry.add("spring.jpa.properties.hibernate.format_sql", () ->"true");
        registry.add("jwkSetUri", () ->"http://stub");
    }

    protected UserSource getUserSource(String userId, String username) {
        return new UserSource() {
            @Override
            public @NonNull String getUserId() {
                return userId;
            }

            @Override
            public @NonNull String getUsername() {
                return username;
            }
        };
    }

    protected UserSource getUserSource(String username) {
        return new UserSource() {
            @Override
            public @NonNull String getUserId() {
                return RandomStringUtils.randomAlphabetic(10);
            }

            @Override
            public @NonNull String getUsername() {
                return username;
            }
        };
    }
}
