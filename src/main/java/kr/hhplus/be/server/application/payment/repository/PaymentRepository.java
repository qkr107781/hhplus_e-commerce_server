package kr.hhplus.be.server.application.payment.repository;

import kr.hhplus.be.server.domain.payment.Payment;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository {

    Payment save(Payment payment);

}
