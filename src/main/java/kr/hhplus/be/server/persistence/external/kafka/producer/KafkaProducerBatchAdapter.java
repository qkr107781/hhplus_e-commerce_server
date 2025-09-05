package kr.hhplus.be.server.persistence.external.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaProducerBatchAdapter<T> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerBatchAdapter(@Qualifier("batchKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // LocalDataTime 직렬화를 위함
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public void sendBatch(String topic, T message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, payload);
            log.info("Sent batch without hashKey message to Kafka. payload={}", payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message: {}", message, e);
            throw new RuntimeException(e);
        }
    }

    public void sendBatch(String topic, String hashKey, T message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, hashKey, payload);
            log.info("Sent batch with hashKey message to Kafka. hashKey={}, payload={}", hashKey, payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message: {}", message, e);
            throw new RuntimeException(e);
        }
    }

}
