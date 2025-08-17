package kr.hhplus.be.server.common.redis;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DistributedLockAspect {

    private static final Logger log = LoggerFactory.getLogger(DistributedLockAspect.class);

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;
    private final ExpressionParser parser = new SpelExpressionParser();

    public DistributedLockAspect(RedissonClient redissonClient,AopForTransaction aopForTransaction) {
        this.redissonClient = redissonClient;
        this.aopForTransaction = aopForTransaction;
    }

    @Around("@annotation(kr.hhplus.be.server.common.redis.DistributedLock)")
    public Object multiLock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DistributedLock distributedLock = signature.getMethod().getAnnotation(DistributedLock.class);

        // SpEL을 사용하여 락 키 리스트를 동적으로 생성
        List<String> lockKeys = generateLockKeys(joinPoint, distributedLock.keys());

        // RedissonMultiLock을 사용하여 여러 락 객체 생성
        RLock[] rLocks = lockKeys.stream()
                                    .map(redissonClient::getLock)
                                    .toArray(RLock[]::new);

        // MultiLock 객체 생성 -> 한번에 여러개의 락을 실행하기 때문에 키 순서 상관 없음
        RedissonMultiLock multiLock = new RedissonMultiLock(rLocks);

        boolean acquired = false;
        try {
            acquired = multiLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), TimeUnit.SECONDS);

            if (!acquired) {
                throw new IllegalStateException("모든 락을 획득할 수 없습니다: " + lockKeys);
            } else{
                log.info("락 획득");
                for (String key : lockKeys){
                    log.info("Redis Key: {}", key);
                }
            }

            return aopForTransaction.proceed(joinPoint);

        } finally {
            if (acquired) {
                multiLock.unlock();
                log.info("락 해제");
            }
        }
    }

    // SpEL을 이용한 락 키 리스트 생성 로직
    private List<String> generateLockKeys(ProceedingJoinPoint joinPoint, String[] keySpels) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        List<String> lockKeys = new ArrayList<>();
        for (String keySpel : keySpels) {
            Object value = parser.parseExpression(keySpel).getValue(context);
            if (value instanceof List) {
                ((List<?>) value).stream().map(Object::toString).forEach(lockKeys::add);
            } else if (value != null) {
                lockKeys.add(value.toString());
            }
        }
        return lockKeys;
    }

    @Around("@annotation(kr.hhplus.be.server.common.redis.DistributedFairLock)")
    public Object fairLock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DistributedFairLock distributedFairLock = signature.getMethod().getAnnotation(DistributedFairLock.class);

        // SpEL을 사용하여 락 키 리스트를 동적으로 생성
        String lockKey = generateLockKey(joinPoint, distributedFairLock.key());

        // RedissonFairLock 사용하여 공정 락 객체 생성
        RLock rFairLock = redissonClient.getFairLock(lockKey);

        boolean acquired = false;
        try {
            acquired = rFairLock.tryLock(distributedFairLock.waitTime(), distributedFairLock.leaseTime(), TimeUnit.SECONDS);

            if (!acquired) {
                throw new IllegalStateException("모든 락을 획득할 수 없습니다: " + rFairLock);
            } else{
                log.info("락 획득");
                log.info("Redis Key: {}", lockKey);
            }

            return aopForTransaction.proceed(joinPoint);

        } finally {
            if (acquired) {
                rFairLock.unlock();
                log.info("락 해제");
            }
        }
    }

    private String generateLockKey(ProceedingJoinPoint joinPoint, String keySpel) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return Objects.requireNonNull(parser.parseExpression(keySpel).getValue(context)).toString();
    }

}