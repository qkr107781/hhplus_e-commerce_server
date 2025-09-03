package kr.hhplus.be.server.persistence.external.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.hhplus.be.server.application.payment.dto.PaymentResponse;
import kr.hhplus.be.server.common.kafka.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
            PaymentResponse.Create response = objectMapper.readValue(message, PaymentResponse.Create.class);
            log.info("Consumed payment event from Kafka. orderId={}, payload={}",response.order().orderId(), message);
        } catch (Exception e) {
            log.error("Failed to deserialize Kafka message: {}", message, e);
        }
    }
}