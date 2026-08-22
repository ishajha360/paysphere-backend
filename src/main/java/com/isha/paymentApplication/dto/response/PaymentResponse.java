package com.isha.paymentApplication.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long paymentId;
    private String orderId;
    private String status;
    private String gatewayRef;
}
