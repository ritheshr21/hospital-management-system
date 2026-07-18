package com.hms.billing_service.dto;

import com.hms.billing_service.entity.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    /** Required for CARD. */
    private String cardLast4;

    /** Required for UPI. */
    private String upiId;

    /** Required for INSURANCE. */
    private String policyNumber;
}
