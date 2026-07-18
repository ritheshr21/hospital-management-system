package com.hms.billing_service.payment;

import java.math.BigDecimal;

/**
 * Outcome of applying a payment strategy to a bill.
 *
 * @param reference        transaction reference to print on the invoice
 * @param note             human-readable summary of how it was settled
 * @param insuranceCovered portion paid by an insurer (ZERO for direct payments)
 * @param collected        portion actually collected from the patient
 */
public record PaymentResult(
        String reference,
        String note,
        BigDecimal insuranceCovered,
        BigDecimal collected) {
}
