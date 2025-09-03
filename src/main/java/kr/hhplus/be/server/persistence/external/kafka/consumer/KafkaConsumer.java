package kr.hhplus.be.server.persistence.external.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.hhplus.be.server.application.payment.event.publisher.PaymentCreateEventPublisher;
import kr.hhplus.be.server.common.kafka.KafkaConstants;
import kr.hhplus.be.server.persistence.external.dataplatform.AsyncDataPlatformSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaConsumer {

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
}