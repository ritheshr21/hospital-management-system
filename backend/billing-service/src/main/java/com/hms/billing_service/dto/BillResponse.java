package com.hms.billing_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.hms.billing_service.entity.BillStatus;
import com.hms.billing_service.entity.PaymentMethod;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillResponse {
    private UUID id;
    private String invoiceNumber;
    private UUID appointmentId;
    private UUID patientId;
    private String patientName;
    private String doctorName;
    private String department;
    private List<BillItemDto> items;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private BillStatus status;
    private PaymentMethod paymentMethod;
    private String paymentReference;
    private BigDecimal insuranceCovered;
    private BigDecimal amountPaid;
    private String paymentNote;
    private Instant paidAt;
    private Instant createdAt;
}
