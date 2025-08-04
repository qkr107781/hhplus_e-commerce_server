package kr.hhplus.be.server.domain.balance;

public class BalanceChargePolicy {
    private BalanceChargePolicy() {
        throw new IllegalStateException("Policy class");
    }

    // 최소/최대 충전 금액 관련 정책
    public static final long MIN_CHARGE_AMOUNT = 1L;
    public static final long MAX_CHARGE_AMOUNT_PER = 100_000L;

    // 잔액 관련 정책
    public static final long OVER_BALANCE = 1_000_000L;
}
