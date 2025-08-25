package kr.hhplus.be.server.application.coupon.service;

import jakarta.annotation.PreDestroy;
import kr.hhplus.be.server.application.coupon.dto.CouponRequest;
import kr.hhplus.be.server.application.coupon.repository.CouponIssuedInfoJdbcRepository;
import kr.hhplus.be.server.common.redis.LuaScript;
import kr.hhplus.be.server.common.redis.RedisKeys;
import org.redisson.api.RStream;
import org.redisson.api.RedissonClient;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamCreateGroupArgs;
import org.redisson.api.stream.StreamReadGroupArgs;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CouponConsumer implements CommandLineRunner {

    private final CouponIssuedInfoJdbcRepository couponIssuedInfoJdbcRepository;
    private final RedissonClient redissonClient;
    private final LuaScript luaScript;

    private final String GROUP_NAME = RedisKeys.COUPON_ISSUE_CONSUMER_GROUP.format();
    private final String CONSUMER_NAME = RedisKeys.COUPON_ISSUE_CONSUMER_NAME.format();
    private ExecutorService executor;

    public CouponConsumer(CouponIssuedInfoJdbcRepository couponIssuedInfoJdbcRepository, RedissonClient redissonClient, LuaScript luaScript) {
        this.couponIssuedInfoJdbcRepository = couponIssuedInfoJdbcRepository;
        this.redissonClient = redissonClient;
        this.luaScript = luaScript;
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }

    @Override
    public void run(String... args) {
        int workerCount = 5; // 동시에 실행할 워커 수
        executor = Executors.newFixedThreadPool(workerCount);

        for (int i = 0; i < workerCount; i++) {
            RStream<String, String> queueStream = redissonClient.getStream(RedisKeys.COUPON_ISSUE_JOB.format());

            try {
                // 그룹 없으면 생성 (already exists 예외는 무시)
                queueStream.createGroup(StreamCreateGroupArgs.name(GROUP_NAME).makeStream());
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("BUSYGROUP")) {
                    //컨슈머 그룹 이미 있음
                } else {
                    throw e;
                }
            }

            final String consumerName = CONSUMER_NAME + "-" + i;
            executor.submit(() -> {
                while (true) {
                    try {
                        // 한 번에 최대 50개 메시지 읽기 - timeout 5초 blocking으로 대기 -> 다른 워커에는 영향 없음
                        Map<StreamMessageId, Map<String, String>> messages = queueStream.readGroup(
                                GROUP_NAME,
                                consumerName,
                                StreamReadGroupArgs.greaterThan(StreamMessageId.NEVER_DELIVERED).count(50).timeout(Duration.ofSeconds(5))
                        );

                        //timeout 대기 동안 메세지 없으면 여기로 이동
                        if (messages == null || messages.isEmpty()) {
                            continue;
                        }

                        // DB insert를 위한 리스트(coupon_issued_info 테이블)
                        List<CouponRequest.Issue> bulkInsertList = new ArrayList<>();

                        // 삭제할 메시지 ID를 저장할 리스트
                        List<StreamMessageId> messageIdsToDelete = new ArrayList<>();

                        for (Map.Entry<StreamMessageId, Map<String, String>> entry : messages.entrySet()) {
                            StreamMessageId messageId = entry.getKey();
                            Map<String, String> data = entry.getValue();

                            String couponId = data.get("couponId");
                            String userId = data.get("userId");

                            Number result = luaScript.decStockFromRedis(redissonClient, couponId);
                            if (result.intValue() != 1) {
                                continue;
                            }

                            // DB bulk insert 준비
                            bulkInsertList.add(new CouponRequest.Issue(Long.parseLong(userId),Long.parseLong(couponId)));

                            // 메시지 ack
                            queueStream.ack(GROUP_NAME, messageId);

                            // 삭제 리스트에 ID 추가
                            messageIdsToDelete.add(messageId);
                        }

                        // 배치 DB insert
                        if (!bulkInsertList.isEmpty()) {
                            try {
                                couponIssuedInfoJdbcRepository.bulkInsertCouponIssuedInfo(bulkInsertList);

                                // DB 저장 성공 시에만 스트림에서 메시지 삭제
                                queueStream.remove(messageIdsToDelete.toArray(new StreamMessageId[0]));
                            } catch (Exception e) {
                                // DB 실패 시 Redis 잔여 쿠폰 복구
                                for (CouponRequest.Issue item : bulkInsertList) {
                                    String couponId =  String.valueOf(item.couponId());
                                    luaScript.incStockFromRedis(redissonClient, couponId);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Redis 끊겼을 경우를 대비해 약간 대기 후 재시도
                        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                    }
                }
            });
        }
    }
}
