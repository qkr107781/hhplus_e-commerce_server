package kr.hhplus.be.server.persistence.payment;

import kr.hhplus.be.server.application.payment.repository.PaymentRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentAdapter implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    public PaymentAdapter(PaymentJpaRepository paymentJpaRepository) {
        this.paymentJpaRepository = paymentJpaRepository;
    }

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
