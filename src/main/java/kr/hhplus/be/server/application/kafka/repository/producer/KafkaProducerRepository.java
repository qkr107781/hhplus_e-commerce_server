package kr.hhplus.be.server.application.kafka.repository.producer;

public interface KafkaProducerRepository<T> {

    void send(String topic, T message);

    void send(String topic, String hashKey, T message);
}
