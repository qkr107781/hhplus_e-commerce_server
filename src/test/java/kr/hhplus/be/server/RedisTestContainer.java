package kr.hhplus.be.server;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class RedisTestContainer {

    private static final int REDIS_PORT = 6379;

    private static final GenericContainer<?> redisContainer =
            new GenericContainer<>(DockerImageName.parse("redis:7.2.4-alpine"))
                    .withExposedPorts(REDIS_PORT);

    static {
        redisContainer.start();
    }

    @Bean(name = "testRedissonClient")
    @Primary
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.setCodec(StringCodec.INSTANCE);
        config.useSingleServer()
                .setAddress("redis://" + redisContainer.getHost() + ":" + redisContainer.getFirstMappedPort());
        return Redisson.create(config);
    }

}
