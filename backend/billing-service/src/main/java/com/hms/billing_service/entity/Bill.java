package com.hms.billing_service.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue
    private UUID id;

    /** Human-friendly invoice number, e.g. INV-2026-000123. */
    @Column(unique = true)
    private String invoiceNumber;

    /** Set when the bill was raised automatically from a completed appointment. */
    @Column(unique = true)
    private UUID appointmentId;

    private UUID patientId;

    @Column(nullable = false)
    private String patientName;

    private String doctorName;

    private String department;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "bill_id")
    @Builder.Default
    private List<BillItem> items = new ArrayList<>();

    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;

    // --- payment ---
    @Enumerated(EnumType.STRING)
    private BillStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String paymentReference;

    /** Portion settled by an insurer (0 for direct payments). */
    @Builder.Default
    private BigDecimal insuranceCovered = BigDecimal.ZERO;

    /** Portion actually collected from the patient. */
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO;

    private String paymentNote;

    private Instant paidAt;

    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = BillStatus.PENDING;
        }
    }
}
