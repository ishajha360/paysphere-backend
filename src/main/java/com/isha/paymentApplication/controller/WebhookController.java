package com.isha.paymentApplication.controller;

import com.isha.paymentApplication.dto.request.WebhookRequest;
import com.isha.paymentApplication.dto.response.ApiResponse;
import com.isha.paymentApplication.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final PaymentService paymentService;

    @PostMapping("/payment-status")
    public ApiResponse<String> handleWebhook(@RequestBody WebhookRequest request) {

        if ("success".equalsIgnoreCase(request.getStatus())) {
            paymentService.markSuccess(request.getGatewayRef());
        } else {
            paymentService.markFailed(request.getGatewayRef());
        }

        return ApiResponse.success("Webhook processed successfully", null);
    }
}
