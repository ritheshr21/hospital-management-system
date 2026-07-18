package com.hms.billing_service.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.hms.billing_service.dto.PaymentRequest;
import com.hms.billing_service.entity.PaymentMethod;

@Component
public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod method() {
        return PaymentMethod.CASH;
    }

    @Override
    public PaymentResult pay(BigDecimal total, PaymentRequest request) {
        String reference = "CASH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(reference, "Collected in cash at the counter", BigDecimal.ZERO, total);
    }
}
