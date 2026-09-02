package com.isha.paymentApplication.controller;

import com.isha.paymentApplication.dto.response.ApiResponse;
import com.isha.paymentApplication.service.PaymentService;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final PaymentService paymentService;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/payment-status")
    public ApiResponse<String> handleWebhook(@RequestBody String payload, @RequestHeader("X-Razorpay-Signature") String signature) {
        try {
            boolean isValid = Utils.verifyWebhookSignature(payload, signature, webhookSecret);
            if (!isValid) {
                return ApiResponse.error("Invalid webhook signature");
            }

            JSONObject json = new JSONObject(payload);
            String event = json.getString("event");
            JSONObject orderEntity = json.getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity");
            String orderId = orderEntity.getString("order_id");

            if ("payment.captured".equals(event)) {
                paymentService.markSuccess(orderId);
            } else if ("payment.failed".equals(event)) {
                paymentService.markFailed(orderId);
            }

            return ApiResponse.success("Webhook processed successfully", null);
        } catch (Exception e) {
            return ApiResponse.error("Webhook processing failed: " + e.getMessage());
        }
    }
}
