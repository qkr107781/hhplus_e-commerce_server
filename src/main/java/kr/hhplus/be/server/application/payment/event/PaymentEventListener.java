package kr.hhplus.be.server.application.payment.event;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.hhplus.be.server.persistence.external.AsyncDataPlatformSender;
import kr.hhplus.be.server.persistence.external.AsyncRedisSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.CompletableFuture;

@Component
public class PaymentEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);

    private final AsyncRedisSender asyncRedisSender;

    public PaymentEventListener(AsyncRedisSender asyncRedisSender) {
        this.asyncRedisSender = asyncRedisSender;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompletedAndSendToDataPlatform(PaymentCreateEvent.SendDataPlatform event) throws JsonProcessingException {
        log.info("결제 완료 후 데이터 플랫폼 API 요청");

        //결제 내역 데이터 플랫폼 API 전송(비동기)
        AsyncDataPlatformSender sender = new AsyncDataPlatformSender("http://testestest.com");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonData = objectMapper.writeValueAsString(event.response());

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
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompletedAndSendToRedis(PaymentCreateEvent.SendRedis event){
        log.info("결제 완료 후 레디스 데이터 입력 요청");
        asyncRedisSender.sendToRedisTop5ProductStatisticsData(event.redisDataList());
        log.info("결제 완료 후 레디스 데이터 입력 설공");
    }
}