package com.isha.paymentApplication.repository;

import com.isha.paymentApplication.entity.Payment;
import com.isha.paymentApplication.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByGatewayRef(String gatewayRef);;
    Optional<Payment> findByOrderId(String orderId);
    List<Payment> findByUserId(Long userId);
    List<Payment> findByStatusAndNextRetryAtBefore(PaymentStatus status, LocalDateTime time);
}
