package kr.hhplus.be.server.common.redis;

public enum RedisKeys {
    COUPON_META("coupon:%s:meta"),
    COUPON_QUEUE("coupon:%s:queue"),
    COUPON_ISSUE_JOB("coupon:queue:issue:job"),
    COUPON_ISSUE_CONSUMER_GROUP("couponGroup"),
    COUPON_ISSUE_CONSUMER_NAME("consumer"),
    DAILY_SALES_PREFIX("daily:sales:");


    private final String pattern;

    RedisKeys(String pattern) {
        this.pattern = pattern;
    }

    public String format(Object... args) {
        return String.format(pattern, args);
    }
}
