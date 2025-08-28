package kr.hhplus.be.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)//웹 환경 구성 하지 않도록 제어
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
@Import({MySQLTestContainer.class, RedisTestContainer.class})//MySQL, Redis 로딩
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestContainersConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @AfterAll
    void cleanDatabase() throws Exception {
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        try (Connection conn = dataSource.getConnection()) {
            // delete.sql 실행
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("delete.sql"));
        }
    }

}