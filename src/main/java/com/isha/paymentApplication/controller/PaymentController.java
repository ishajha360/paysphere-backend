package com.isha.paymentApplication.controller;

import com.isha.paymentApplication.dto.request.InitiatePaymentRequest;
import com.isha.paymentApplication.dto.response.ApiResponse;
import com.isha.paymentApplication.dto.response.PaymentResponse;
import com.isha.paymentApplication.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ApiResponse<PaymentResponse> initiate(@RequestBody InitiatePaymentRequest request, HttpServletRequest httpRequest) throws Exception {
        String ip = httpRequest.getRemoteAddr();
        PaymentResponse response = paymentService.initiatePayment(request , ip);
        return ApiResponse.success("Payment initiated successfully", response);
    }
    @GetMapping("/history/{userId}")
    public ApiResponse<List<PaymentResponse>> getHistory(@PathVariable Long userId) {
        List<PaymentResponse> history = paymentService.getPaymentHistory(userId);
        return ApiResponse.success("Payment history fetched successfully", history);
    }
}
