package kr.hhplus.be.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = TestContainersConfiguration.class)//Spring boot Context 로딩 전 TestContainerConfiguration 읽어오게 하기 위함
class ServerApplicationTests {

	@Test
	void contextLoads() {
	}

}
