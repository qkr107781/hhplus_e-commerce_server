package kr.hhplus.be.server.persistence.external.redis;

import jakarta.annotation.PreDestroy;
import kr.hhplus.be.server.application.coupon.service.CouponUseCase;
import kr.hhplus.be.server.application.redis.repository.RedisRepository;
import kr.hhplus.be.server.common.redis.RedisKeys;
import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamReadGroupArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CouponConsumer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CouponConsumer.class);

    private final CouponUseCase couponUseCase;
    private final RedisRepository redisRepository;

    private ExecutorService executor;

    private final String GROUP_NAME = RedisKeys.COUPON_ISSUE_CONSUMER_GROUP.format();

    public CouponConsumer(CouponUseCase couponUseCase, RedisRepository redisRepository) {
        this.couponUseCase = couponUseCase;
        this.redisRepository = redisRepository;
    }

    @PreDestroy
    public void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
            log.info("CouponConsumer executor shutdown");
        }
    }

    @Override
    public void run(String... args) {
        String consumerName = RedisKeys.COUPON_ISSUE_CONSUMER_NAME.format();

        int workerCount = 5; // 동시에 실행할 워커 수
        executor = Executors.newFixedThreadPool(workerCount);

        RStream<String, String> queueStream = redisRepository.initConsumerGroup(GROUP_NAME);

        for (int i = 0; i < workerCount; i++) {
            final String finalConsumerName = consumerName + "-" + i;
            executor.submit(() -> consumerLoop(queueStream, GROUP_NAME, finalConsumerName));
        }
    }

    private void consumerLoop(RStream<String, String> queueStream, String groupName, String consumerName) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // 한 번에 최대 50개 메시지 읽기 - timeout 5초 blocking으로 대기 -> 다른 워커에는 영향 없음
                Map<StreamMessageId, Map<String, String>> messages = queueStream.readGroup(
                        groupName,
                        consumerName,
                        StreamReadGroupArgs.greaterThan(StreamMessageId.NEVER_DELIVERED).count(50).timeout(Duration.ofSeconds(5))
                );
                if (messages == null || messages.isEmpty()) continue;

                couponUseCase.couponIssueProcess(queueStream, groupName, messages);
            } catch (Exception e) {
                log.warn("Error while reading messages, retrying...", e);
                // Redis 끊겼을 경우를 대비해 약간 대기 후 재시도
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        }
    }
}
