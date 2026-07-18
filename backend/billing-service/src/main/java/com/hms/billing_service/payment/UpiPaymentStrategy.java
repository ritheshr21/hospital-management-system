package com.hms.billing_service.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.hms.billing_service.dto.PaymentRequest;
import com.hms.billing_service.entity.PaymentMethod;
import com.hms.billing_service.exception.InvalidPaymentException;

@Component
public class UpiPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod method() {
        return PaymentMethod.UPI;
    }

    @Override
    public PaymentResult pay(BigDecimal total, PaymentRequest request) {
        String upiId = request.getUpiId();
        if (upiId == null || !upiId.matches("[\\w.\\-]+@[\\w.\\-]+")) {
            throw new InvalidPaymentException("UPI payment requires a valid UPI id, e.g. name@bank");
        }
        String reference = "UPI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(reference, "Paid via UPI (" + upiId + ")", BigDecimal.ZERO, total);
    }
}
