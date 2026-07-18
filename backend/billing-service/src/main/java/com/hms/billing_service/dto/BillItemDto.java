package com.hms.billing_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BillItemDto {

    private UUID id;

    @NotBlank(message = "Item description is required")
    private String description;

    @Positive(message = "Quantity must be at least 1")
    private int quantity = 1;

    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;

    private BigDecimal lineTotal;
}
