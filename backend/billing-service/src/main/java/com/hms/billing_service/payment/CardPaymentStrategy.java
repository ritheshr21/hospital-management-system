package com.hms.billing_service.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.hms.billing_service.dto.PaymentRequest;
import com.hms.billing_service.entity.PaymentMethod;
import com.hms.billing_service.exception.InvalidPaymentException;

@Component
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod method() {
        return PaymentMethod.CARD;
    }

    @Override
    public PaymentResult pay(BigDecimal total, PaymentRequest request) {
        String last4 = request.getCardLast4();
        if (last4 == null || !last4.matches("\\d{4}")) {
            throw new InvalidPaymentException("Card payment requires the last 4 digits of the card");
        }
        String reference = "CARD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(reference, "Paid by card ending " + last4, BigDecimal.ZERO, total);
    }
}
