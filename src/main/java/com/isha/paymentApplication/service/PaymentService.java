package com.isha.paymentApplication.service;

import com.isha.paymentApplication.dto.request.InitiatePaymentRequest;
import com.isha.paymentApplication.dto.response.PaymentResponse;
import com.isha.paymentApplication.entity.Payment;
import com.isha.paymentApplication.entity.PaymentStatus;
import com.isha.paymentApplication.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final FraudCheckService fraudCheckService;

    public PaymentResponse initiatePayment(InitiatePaymentRequest request , String ipAddress) {
        boolean blocked = fraudCheckService.isBlocked(request.getCardHash(), ipAddress);
        Payment payment = new Payment();
        payment.setOrderId(UUID.randomUUID().toString());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setCardHash(request.getCardHash());
        payment.setIpAddress(ipAddress);


        if (blocked) {
            payment.setStatus(PaymentStatus.BLOCKED);
            paymentRepository.save(payment);
            throw new RuntimeException("Too many attempts detected. This payment has been blocked.");
        }
        payment.setStatus(PaymentStatus.PENDING);
        payment.setGatewayRef("gw_" + UUID.randomUUID());

        Payment saved = paymentRepository.save(payment);

        return new PaymentResponse(
                saved.getId(),
                saved.getOrderId(),
                saved.getStatus().name(),
                saved.getGatewayRef(),
                saved.getAmount()
        );

    }
    public void markSuccess(String gatewayRef) {
        Payment payment = paymentRepository.findByGatewayRef(gatewayRef)
                .orElseThrow(() -> new RuntimeException("Payment not found for gatewayRef: " + gatewayRef));

        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
    }

    public void markFailed(String gatewayRef) {
        Payment payment = paymentRepository.findByGatewayRef(gatewayRef)
                .orElseThrow(() -> new RuntimeException("Payment not found for gatewayRef: " + gatewayRef));

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }
    public List<PaymentResponse> getPaymentHistory(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);

        return payments.stream()
                .map(p -> new PaymentResponse(
                        p.getId(),
                        p.getOrderId(),
                        p.getStatus().name(),
                        p.getGatewayRef(),
                        p.getAmount()
                ))
                .collect(Collectors.toList());
    }
}
