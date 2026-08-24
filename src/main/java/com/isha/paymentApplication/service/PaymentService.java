package com.isha.paymentApplication.service;

import com.isha.paymentApplication.dto.request.InitiatePaymentRequest;
import com.isha.paymentApplication.dto.response.PaymentResponse;
import com.isha.paymentApplication.entity.Payment;
import com.isha.paymentApplication.entity.PaymentStatus;
import com.isha.paymentApplication.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentResponse initiatePayment(InitiatePaymentRequest request) {

        Payment payment = new Payment();
        payment.setOrderId(UUID.randomUUID().toString());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setCardHash(request.getCardHash());
        payment.setStatus(PaymentStatus.PENDING);

        payment.setGatewayRef("gw_" + UUID.randomUUID());

        Payment saved = paymentRepository.save(payment);

        return new PaymentResponse(
                saved.getId(),
                saved.getOrderId(),
                saved.getStatus().name(),
                saved.getGatewayRef()
        );
    }
}
