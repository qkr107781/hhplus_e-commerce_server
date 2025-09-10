package kr.hhplus.be.server;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainerConnections {

    private static final Network network = Network.newNetwork();

    // ===== MySQL =====
    @ServiceConnection
    @Bean
    public static MySQLContainer<?> mysqlContainer() {
        return MySQLHolder.INSTANCE;
    }

    private static class MySQLHolder{
        private static final MySQLContainer<?> INSTANCE =
                new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"))
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withNetwork(network)
                .withNetworkAliases("mysql-server");
        static {INSTANCE.start();}
    }

    // ===== Redis =====
    @Bean
    public static GenericContainer<?> redisContainer() {
        return RedisHolder.INSTANCE;
    }

    private static class RedisHolder{
        private static final GenericContainer INSTANCE =
                new GenericContainer<>(DockerImageName.parse("redis:7.2.4-alpine"))
                        .withExposedPorts(6379)
                        .withNetwork(network)
                        .withNetworkAliases("redis-server");
        static {INSTANCE.start();}
    }

    // ===== Kafka =====
    @ServiceConnection
    @Bean
    public static KafkaContainer kafkaContainer() {
        return KafkaHolder.INSTANCE;
    }

    private static class KafkaHolder{
        private static final KafkaContainer INSTANCE =
                new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
                        .withNetwork(network)
                        .withNetworkAliases("kafka-server");
        static {INSTANCE.start();}
    }

    // ===== Debezium + Kafka Connect =====
    @Bean
    public static GenericContainer<?> kafkaConnectContainer() {
        return KafkaConnectHolder.INSTANCE;
    }

    private static class KafkaConnectHolder{
        private static final GenericContainer INSTANCE =
                new GenericContainer<>(DockerImageName.parse("debezium/connect:2.5"))
                        .withExposedPorts(8083)
                        .withNetwork(network)
                        .withEnv("BOOTSTRAP_SERVERS", KafkaHolder.INSTANCE.getBootstrapServers()) // 내부 네트워크 별칭 사용
                        .withEnv("GROUP_ID", "connect-cluster")
                        .withEnv("CONFIG_STORAGE_TOPIC", "connect-configs")
                        .withEnv("OFFSET_STORAGE_TOPIC", "connect-offsets")
                        .withEnv("STATUS_STORAGE_TOPIC", "connect-status")
                        .withEnv("PLUGIN_PATH", "/kafka/connect")
                        .dependsOn(KafkaHolder.INSTANCE, MySQLHolder.INSTANCE);
        static {INSTANCE.start();}
    }

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry
    ,KafkaContainer kafkaContainer, GenericContainer<?> kafkaConnectContainer) {
        // Kafka
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);

        // Debezium Connect
        registry.add("debezium.connect.url",
                () -> "http://" + kafkaConnectContainer.getHost() + ":" + kafkaConnectContainer.getFirstMappedPort());
    }

    @Bean
    @Primary
    public RedissonClient redissonClient(GenericContainer<?> redisContainer) {
        Config config = new Config();
        config.setCodec(StringCodec.INSTANCE);
        config.useSingleServer()
                .setAddress("redis://" + redisContainer.getHost() + ":" + redisContainer.getFirstMappedPort());
        return Redisson.create(config);
    }
}
