package kr.hhplus.be.server.common.kafka;

public final class KafkaConstants {

    public static final String PAYMENT_COMPLETE_TOPIC = "payment-complete-topic";
    public static final String PAYMENT_COMPLETE_GROUP = "payment-complete-group";

    public static final String COUPON_ISSUED_TOPIC = "coupon-issued-topic";
    public static final String COUPON_ISSUED_GROUP = "coupon-issue-group";

    // 인스턴스 생성을 막기 위함
    private KafkaConstants() {}
}
