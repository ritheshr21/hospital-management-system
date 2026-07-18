package com.hms.billing_service.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Published to "bill_paid" once a bill is fully settled. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillPaidEvent {
    private UUID billId;
    private String invoiceNumber;
    private UUID appointmentId;
    private UUID patientId;
    private String patientName;
    private BigDecimal total;
    private BigDecimal insuranceCovered;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private String paymentReference;
    private Instant paidAt;
}
