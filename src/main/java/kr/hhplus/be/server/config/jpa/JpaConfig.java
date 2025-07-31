package kr.hhplus.be.server.config.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories
public class JpaConfig {

    @PersistenceContext // EntityManager를 주입받기 위한 어노테이션
    private EntityManager entityManager;

    @Bean // 이 메서드가 반환하는 객체를 스프링 빈으로 등록
    public JPAQueryFactory jpaQueryFactory() {
        // entityManager를 사용하여 JPAQueryFactory 인스턴스를 생성하고 반환
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }


}