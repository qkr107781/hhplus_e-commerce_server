package kr.hhplus.be.server.common.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    // SpEL 표현식으로 락 키 리스트를 받을 수 있게 keys 속성을 정의
    String[] keys();

    long waitTime() default 10L;
    long leaseTime() default 5L;
}