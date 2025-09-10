package kr.hhplus.be.server.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    private final KafkaConnectionDetails kafkaConnectionDetails;

    public KafkaConsumerConfig(KafkaConnectionDetails kafkaConnectionDetails) {
        this.kafkaConnectionDetails = kafkaConnectionDetails;
    }

    private Map<String, Object> baseConsumerConfig() {
        Map<String, Object> props = new HashMap<>();
        String bootstrap = String.join(",", kafkaConnectionDetails.getBootstrapServers());
        if (bootstrap.isBlank()) {
            throw new IllegalStateException("No Kafka bootstrap servers provided. If you're running tests, ensure @ServiceConnection KafkaContainer is present OR set TEST_KAFKA_BOOTSTRAP_SERVERS.");
        }
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        // Key/Value에 ErrorHandlingDeserializer 적용
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        // Delegate deserializer for key and value
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        // JSON Deserializer properties
        props.put("spring.json.use.type.headers", false);
        props.put("spring.json.trusted.packages", "*");
        props.put("spring.json.value.default.type", "java.util.Map");// -> 지정 타입인데 공통으로 쓸거라 Map으로 지정
        props.put("spring.json.fail.on.unknown.properties", false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    // record Consumer Group
    @Bean(name = "recordKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> recordKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(baseConsumerConfig()));
        return factory;
    }

    // batch Consumer Group
    @Bean(name = "batchKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> batchKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(baseConsumerConfig()));
        factory.setBatchListener(true);
        return factory;
    }
}