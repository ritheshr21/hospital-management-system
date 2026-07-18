package com.hms.pharmacy_service.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PrescriptionItemDto {

    private UUID id;

    @NotNull(message = "Medicine is required")
    private UUID medicineId;

    private String medicineName;

    private String dosage;

    private int durationDays;

    @Positive(message = "Quantity must be positive")
    private int quantity;

    private String instructions;
}
