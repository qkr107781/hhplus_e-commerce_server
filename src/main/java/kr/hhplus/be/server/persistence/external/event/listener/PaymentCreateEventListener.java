package kr.hhplus.be.server.persistence.external.event.listener;


import kr.hhplus.be.server.application.kafka.repository.producer.KafkaProducerRepository;
import kr.hhplus.be.server.application.payment.event.publisher.PaymentCreateEventPublisher;
import kr.hhplus.be.server.common.kafka.KafkaConstants;
import kr.hhplus.be.server.persistence.external.redis.AsyncRedisSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentCreateEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentCreateEventListener.class);

    private final AsyncRedisSender asyncRedisSender;
    private final KafkaProducerRepository<PaymentCreateEventPublisher.SendDataPlatform> kafkaProducerRepository;

    public PaymentCreateEventListener(AsyncRedisSender asyncRedisSender, KafkaProducerRepository<PaymentCreateEventPublisher.SendDataPlatform> kafkaProducerRepository) {
        this.asyncRedisSender = asyncRedisSender;
        this.kafkaProducerRepository = kafkaProducerRepository;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompletedAndSendToDataPlatform(PaymentCreateEventPublisher.SendDataPlatform event) {
        kafkaProducerRepository.send(KafkaConstants.PAYMENT_COMPLETE_TOPIC,event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompletedAndSendToRedis(PaymentCreateEventPublisher.SendRedis event){
        log.info("결제 완료 후 레디스 데이터 입력 요청");
        asyncRedisSender.sendToRedisTop5ProductStatisticsData(event.redisDataList());
        log.info("결제 완료 후 레디스 데이터 입력 설공");
    }
}