package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class Payment {

    private Long paymentId;
    private Long userId;
    private Long orderId;
    private Long paymentPrice;
    private LocalDateTime paymentDate;

    public Payment(Long paymentId, Long userId, Long orderId, Long paymentPrice, LocalDateTime paymentDate) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.orderId = orderId;
        this.paymentPrice = paymentPrice;
        this.paymentDate = paymentDate;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getPaymentPrice() {
        return paymentPrice;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
}