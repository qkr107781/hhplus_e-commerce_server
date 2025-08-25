package kr.hhplus.be.server;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfiguration { // @TestConfiguration 어노테이션은 유지

	//########################## MySQL TestContainer Configuration ##########################//

	@ServiceConnection
	@Bean
	public static MySQLContainer<?> mySQLContainer() {
		return MySQLHolder.INSTANCE;
	}

	private static class MySQLHolder {
		private static final MySQLContainer<?> INSTANCE =
				new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"))
						.withDatabaseName("testdb")
						.withUsername("test")
						.withPassword("test");
		static { INSTANCE.start(); }
	}

	//########################## Redis TestContainer Configuration ##########################//


	private static final int REDIS_PORT = 6379;

	private static final GenericContainer<?> redisContainer =
			new GenericContainer<>(DockerImageName.parse("redis:7.2.4-alpine"))
					.withExposedPorts(REDIS_PORT);

	static {
		redisContainer.start();
	}

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.setCodec(StringCodec.INSTANCE);
		config.useSingleServer()
				.setAddress("redis://" + redisContainer.getHost() + ":" + redisContainer.getFirstMappedPort());
		return Redisson.create(config);
	}
}