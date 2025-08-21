package kr.hhplus.be.server.application.coupon.service;

import jakarta.annotation.PreDestroy;
import kr.hhplus.be.server.application.coupon.repository.CouponIssuedInfoJdbcRepository;
import kr.hhplus.be.server.common.redis.LuaScript;
import org.redisson.api.RStream;
import org.redisson.api.RedissonClient;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamCreateGroupArgs;
import org.redisson.api.stream.StreamReadGroupArgs;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
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

    private final String STREAM_KEY = "coupon:queue:issue:job";
    private final String GROUP_NAME = "couponGroup";
    private ExecutorService executor;

    public CouponConsumer(CouponIssuedInfoJdbcRepository couponIssuedInfoJdbcRepository, RedissonClient redissonClient, LuaScript luaScript) {
        this.couponIssuedInfoJdbcRepository = couponIssuedInfoJdbcRepository;
        this.redissonClient = redissonClient;
        this.luaScript = luaScript;
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("워커 쓰레드 종료");
        executor.shutdownNow();
    }

    @Override
    public void run(String... args) {
        int workerCount = 5; // 동시에 실행할 워커 수
        executor = Executors.newFixedThreadPool(workerCount);

        for (int i = 0; i < workerCount; i++) {
            RStream<String, String> queueStream = redissonClient.getStream(STREAM_KEY);

            try {
                // 그룹 없으면 생성 (already exists 예외는 무시)
                queueStream.createGroup(StreamCreateGroupArgs.name(GROUP_NAME).makeStream());
//                System.out.println("Created consumer group "+ GROUP_NAME);
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("BUSYGROUP")) {
//                    System.out.println("Consumer group "+GROUP_NAME+" already exists");
                } else {
                    throw e;
                }
            }

            final String consumerName = "worker-" + i;
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
//                            System.out.println(consumerName + " - " + "대기 종료: "+LocalDateTime.now());
                            continue;
                        }

                        // DB insert를 위한 리스트(coupon_issued_info 테이블)
                        List<String> bulkInsertList = new ArrayList<>();

                        // 삭제할 메시지 ID를 저장할 리스트
                        List<StreamMessageId> messageIdsToDelete = new ArrayList<>();

                        System.out.println( consumerName + " - " + "메세지 큐 사이즈: " + messages.size() + " - " + "레디스 발급 시작: " +LocalDateTime.now());
                        for (Map.Entry<StreamMessageId, Map<String, String>> entry : messages.entrySet()) {
//                            System.out.println("메세지 타입: " +entry.getClass() +"메세지 데이터: " + entry);
                            StreamMessageId messageId = entry.getKey();
//                            System.out.println("키: " + messageId);
                            Map<String, String> data = entry.getValue();
//                            System.out.println("값: " + data);

                            String couponId = data.get("couponId");
                            String userId = data.get("userId");

                            Number result = luaScript.decStockFromRedis(redissonClient, couponId);
                            if (result.intValue() != 1) {
                                continue;
                            }

                            // DB bulk insert 준비
                            bulkInsertList.add(couponId + ":" + userId);

                            // 메시지 ack
                            queueStream.ack(GROUP_NAME, messageId);

                            // 삭제 리스트에 ID 추가
                            messageIdsToDelete.add(messageId);
                        }
                        System.out.println(consumerName + " - " + "레디스 발급 종료: " + LocalDateTime.now());

                        // 배치 DB insert
                        if (!bulkInsertList.isEmpty()) {
                            try {
                                System.out.println(consumerName+" - 벌크 Insert/Update 시작: " + bulkInsertList.size() + " - " + LocalDateTime.now());
                                couponIssuedInfoJdbcRepository.bulkInsertCouponIssuedInfo(bulkInsertList);
                                System.out.println(consumerName+" - 벌크 Insert/Update 종료: " + bulkInsertList.size() + " - " + LocalDateTime.now());

                                // DB 저장 성공 시에만 스트림에서 메시지 삭제
                                queueStream.remove(messageIdsToDelete.toArray(new StreamMessageId[0]));
                                System.out.println(consumerName + " - " + messageIdsToDelete.size() + "개의 메시지 스트림에서 삭제 완료");
                            } catch (Exception e) {
                                // DB 실패 시 Redis 잔여 쿠폰 복구
                                for (String item : bulkInsertList) {
                                    String[] arr = item.split(":");
                                    String couponId = arr[0];
                                    String userId = arr[1];

                                    luaScript.incStockFromRedis(redissonClient, couponId);
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("워커 에러: " + consumerName + " - " + e.getMessage());
                        // Redis 끊겼을 경우를 대비해 약간 대기 후 재시도
                        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                    }
                }
            });
        }
    }
}
