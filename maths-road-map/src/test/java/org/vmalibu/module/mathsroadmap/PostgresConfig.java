package org.vmalibu.module.mathsroadmap;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class PostgresConfig {

    private static final String DATABASE_NAME = "test-db";
    private static final String INIT_SCRIPT_PATH = "db/setup.sql";

    @Bean(destroyMethod = "stop")
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:14.7")
                .withDatabaseName(DATABASE_NAME)
                .withInitScript(INIT_SCRIPT_PATH);
    }
}
