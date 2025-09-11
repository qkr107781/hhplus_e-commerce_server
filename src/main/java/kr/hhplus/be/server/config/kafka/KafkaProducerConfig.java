package kr.hhplus.be.server.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private final KafkaConnectionDetails kafkaConnectionDetails;

    public KafkaProducerConfig(KafkaConnectionDetails kafkaConnectionDetails) {
        this.kafkaConnectionDetails = kafkaConnectionDetails;
    }

    // 기본 ProducerFactory
    private Map<String, Object> baseProducerConfig() {
        Map<String, Object> props = new HashMap<>();
        String bootstrap = String.join(",", kafkaConnectionDetails.getBootstrapServers());
        if (bootstrap.isBlank()) {
            throw new IllegalStateException("No Kafka bootstrap servers provided. If you're running tests, ensure @ServiceConnection KafkaContainer is present OR set TEST_KAFKA_BOOTSTRAP_SERVERS.");
        }
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    // record Producer
    @Bean(name = "recordKafkaTemplate")
    public KafkaTemplate<String, Object> recordKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(baseProducerConfig()));
    }

    // batch Producer
    @Bean(name = "batchKafkaTemplate")
    public KafkaTemplate<String, Object> batchKafkaTemplate() {
        Map<String, Object> batchConfig = new HashMap<>(baseProducerConfig());
        batchConfig.put(ProducerConfig.LINGER_MS_CONFIG, 100); // 100ms 대기 (linger.ms)
        batchConfig.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // 16KB 배치 크기 (batch.size)
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(batchConfig));
    }
}