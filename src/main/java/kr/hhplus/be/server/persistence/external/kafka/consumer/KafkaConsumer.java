package kr.hhplus.be.server.persistence.external.kafka.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.hhplus.be.server.application.coupon.dto.CouponOutboxBuilder;
import kr.hhplus.be.server.application.coupon.repository.CouponIssuedInfoRepository;
import kr.hhplus.be.server.application.coupon.repository.CouponOutboxRepository;
import kr.hhplus.be.server.application.coupon.repository.CouponRepository;
import kr.hhplus.be.server.application.payment.event.publisher.PaymentCreateEventPublisher;
import kr.hhplus.be.server.common.kafka.KafkaConstants;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import kr.hhplus.be.server.persistence.external.dataplatform.AsyncDataPlatformSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaConsumer {

    private final CouponRepository couponRepository;
    private final CouponIssuedInfoRepository couponIssuedInfoRepository;
    private final CouponOutboxRepository couponOutboxRepository;

    public KafkaConsumer(CouponRepository couponRepository, CouponIssuedInfoRepository couponIssuedInfoRepository, CouponOutboxRepository couponOutboxRepository) {
        this.couponRepository = couponRepository;
        this.couponIssuedInfoRepository = couponIssuedInfoRepository;
        this.couponOutboxRepository = couponOutboxRepository;
    }

    // LocalDataTime 역직렬화를 위함
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @KafkaListener(
            topics = KafkaConstants.PAYMENT_COMPLETE_TOPIC,
            groupId = KafkaConstants.PAYMENT_COMPLETE_GROUP,
            containerFactory = "recordKafkaListenerContainerFactory"
    )
    public void consumePaymentComplete(String message) {
        try {
            PaymentCreateEventPublisher.SendDataPlatform response = objectMapper.readValue(message, PaymentCreateEventPublisher.SendDataPlatform.class);
            log.info("Consumed payment event from Kafka. paymentId={}, payload={}",response.response().paymentId(), message);

            log.info("결제 완료 후 데이터 플랫폼 API 요청");

            //결제 내역 데이터 플랫폼 API 전송(비동기)
            AsyncDataPlatformSender sender = new AsyncDataPlatformSender("http://testestest.com");

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String jsonData = objectMapper.writeValueAsString(response.response());

            //데이터 전송
            CompletableFuture<Boolean> future1 = sender.sendDataAsync(jsonData);
            future1.thenAccept(success -> {
                if (success) {
                    //성공
                    log.info("결제 완료 후 데이터 플랫폼 API 응답 성공");
                } else {
                    //실패
                    log.info("결제 완료 후 데이터 플랫폼 API 응답 실패");
                }
            });
        } catch (Exception e) {
            log.error("Failed to deserialize Kafka message: {}", message, e);
        }
    }

    @KafkaListener(
            topics = "outbox.hhplus.outbox_event", //동적으로 생성해서 outbox 테이블 topicKey 컬럼에 추가한거 읽어옴
            groupId = KafkaConstants.COUPON_ISSUED_GROUP,
            containerFactory = "recordKafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeCouponIssuing(ConsumerRecord<String, String> record){

        String dynamicKey = record.key(); // "coupon_{couponId}_{userId}_outbox"
        String value = record.value();

        try {
            JsonNode root = objectMapper.readTree(value);
            JsonNode after = root.path("after"); // Insert/Update된 row 데이터
            if (after.isMissingNode()) {
                return; // Delete 이벤트 등은 무시
            }

            long couponId = after.path("coupon_id").asLong();
            long userId = after.path("user_id").asLong();
            long outboxId = after.path("outbox_id").asLong();

            log.info("######### consume from outbox table ###########");
            log.info("Dynamic Key: {}", dynamicKey);
            log.info("Coupon ID: {}", couponId);
            log.info("User ID: {}", userId);

            //1. 잔여 수량 체크
            if (hasRemainingCoupon(couponId) == 0) {
                log.info("잔여 쿠폰 없음");
                return;
            }

            //2. 쿠폰 발급
            issueCoupon(couponId, userId);

            //3. outbox 데이터 status 변경 - pending -> completed
            updateOutboxStatus(couponId,userId,outboxId);

        } catch (Exception e) {
            log.error("DLQ로 전송");
            // 필요시 DeadLetterQueue 처리 + outbox 테이블 상태 fail로 변경
        }
    }

    private int hasRemainingCoupon(long couponId) {
        return couponRepository.decreaseRemainingCoupon(couponId);
    }

    private void issueCoupon(long couponId, long userId) {
        CouponIssuedInfo issuingCoupon = CouponIssuedInfo.builder()
                .userId(userId)
                .useYn("N")
                .issuedAt(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(24L))
                .couponId(couponId)
                .build();

        couponIssuedInfoRepository.issuingCoupon(issuingCoupon);
        log.info("쿠폰 발급 완료: {} -> {}", couponId, userId);
    }

    private void updateOutboxStatus(long couponId, long userId, long outboxId) {
        CouponOutboxBuilder.Update outbox = new CouponOutboxBuilder.Update(
                outboxId,
                userId,
                couponId,
                "completed"
        );
        couponOutboxRepository.save(CouponOutboxBuilder.Update.toDomain(outbox));
        log.info("Outbox 상태(pending -> completed) 업데이트 완료: {}", outboxId);
    }
}