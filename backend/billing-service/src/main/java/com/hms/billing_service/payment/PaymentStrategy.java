package com.hms.billing_service.payment;

import java.math.BigDecimal;

import com.hms.billing_service.dto.PaymentRequest;
import com.hms.billing_service.entity.PaymentMethod;

/**
 * Strategy pattern: each payment method validates its own inputs and decides
 * how the bill total is settled. Adding a new method means adding one class —
 * no changes to BillService.
 */
public interface PaymentStrategy {

    /** The method this strategy handles. */
    PaymentMethod method();

    /**
     * Settles {@code total} for the bill.
     *
     * @throws com.hms.billing_service.exception.InvalidPaymentException if the
     *         request is missing details this method requires
     */
    PaymentResult pay(BigDecimal total, PaymentRequest request);
}
