package kr.hhplus.be.server;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfiguration { // @TestConfiguration 어노테이션은 유지

	//########################## MySQL TestContainer Configuration ##########################//

	@Bean // Spring 빈으로 등록
	@ServiceConnection // <<-- MySQLContainer를 DataSource에 자동으로 연결하도록 지시
	public MySQLContainer<?> mySQLContainer() {
		System.out.println("########## TestcontainersConfiguration: @ServiceConnection MySQL 컨테이너 빈 생성 시작 ##########");
		MySQLContainer<?> container = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"))
				.withDatabaseName("testdb")
				.withUsername("test")
				.withPassword("test");
		// 컨테이너.start()는 @ServiceConnection이 알아서 해줍니다. 여기에 명시적으로 호출하지 마세요.
		System.out.println("########## TestcontainersConfiguration: @ServiceConnection MySQL 컨테이너 빈 생성 완료 ##########");
		return container;
	}

	//########################## Redis TestContainer Configuration ##########################//

	private static final int REDIS_PORT = 6379;

	// Testcontainers로 Redis 컨테이너 정의. Redis 클러스터가 아닌 단일 서버를 사용
	// Redis의 기본 포트인 6379를 노출
	@Container
	private static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2.4-alpine"))
			.withExposedPorts(6379);

	static {
		// Testcontainers 컨테이너를 시작하고,
		// 시스템 프로퍼티에 동적으로 할당된 Redis 서버 주소를 설정
		redis.start();
		String redisAddress = "redis://" + redis.getHost() + ":" + redis.getFirstMappedPort();
		System.setProperty("redisson.address", redisAddress);
	}

	// RedissonClient 빈을 직접 생성하고 동적 주소를 설정
	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		String redisAddress = System.getProperty("redisson.address");
		config.useSingleServer().setAddress(redisAddress);
		return Redisson.create(config);
	}
}