package kr.hhplus.be.server.application.payment.service;

import kr.hhplus.be.server.application.payment.dto.PaymentRequest;
import kr.hhplus.be.server.application.payment.dto.PaymentResponse;

public interface PaymentUseCase {

    /**
     * 결제
     * @param request: 사용자 ID, 주문 ID
     * @return PaymentResponse.Create
     */
    PaymentResponse.Create createPayment(PaymentRequest.Create request) throws Exception;

}
