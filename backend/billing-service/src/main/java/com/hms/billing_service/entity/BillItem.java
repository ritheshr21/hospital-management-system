package com.hms.billing_service.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bill_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillItem {

    @Id
    @GeneratedValue
    private UUID id;

    private String description;

    @Builder.Default
    private int quantity = 1;

    private BigDecimal unitPrice;

    /** quantity * unitPrice, stored so historical invoices never change. */
    private BigDecimal lineTotal;
}
