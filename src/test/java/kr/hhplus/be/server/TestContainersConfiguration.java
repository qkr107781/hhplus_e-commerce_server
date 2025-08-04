package kr.hhplus.be.server;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfiguration { // @TestConfiguration 어노테이션은 유지

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
}