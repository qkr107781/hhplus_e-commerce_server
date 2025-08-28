package kr.hhplus.be.server;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class MySQLTestContainer {

    @ServiceConnection
    @Bean
    public static org.testcontainers.containers.MySQLContainer<?> mySQLContainer() {
        return MySQLHolder.INSTANCE;
    }

    private static class MySQLHolder {
        private static final org.testcontainers.containers.MySQLContainer<?> INSTANCE =
                new org.testcontainers.containers.MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"))
                        .withDatabaseName("testdb")
                        .withUsername("test")
                        .withPassword("test");
        static { INSTANCE.start(); }
    }

}
