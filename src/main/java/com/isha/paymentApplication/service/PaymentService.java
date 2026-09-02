package com.isha.paymentApplication.service;

import com.isha.paymentApplication.dto.request.InitiatePaymentRequest;
import com.isha.paymentApplication.dto.response.PaymentResponse;
import com.isha.paymentApplication.entity.Payment;
import com.isha.paymentApplication.entity.PaymentStatus;
import com.isha.paymentApplication.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final FraudCheckService fraudCheckService;
    @Value("${RAZORPAY_KEY_ID}")
    private String razorpayKeyId;

    @Value("${RAZORPAY_KEY_SECRET}")
    private String razorpayKeySecret;

    public PaymentResponse initiatePayment(InitiatePaymentRequest request , String ipAddress) throws Exception {
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
        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).intValue());
        orderRequest.put("currency", request.getCurrency());
        orderRequest.put("receipt", payment.getOrderId());

        Order razorpayOrder = razorpay.orders.create(orderRequest);

        payment.setStatus(PaymentStatus.PENDING);
        payment.setGatewayRef(razorpayOrder.get("id"));

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
