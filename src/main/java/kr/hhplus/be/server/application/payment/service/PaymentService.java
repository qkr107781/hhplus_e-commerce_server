package kr.hhplus.be.server.application.payment.service;

import kr.hhplus.be.server.application.payment.repository.PaymentRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
