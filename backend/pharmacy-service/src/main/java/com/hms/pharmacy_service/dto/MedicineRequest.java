package com.hms.pharmacy_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hms.pharmacy_service.entity.MedicineUnit;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MedicineRequest {

    @NotBlank(message = "Medicine name is required")
    private String name;

    private String brand;

    private String category;

    private MedicineUnit unit;

    private BigDecimal price;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stockQuantity;

    @Min(value = 0, message = "Reorder level cannot be negative")
    private int reorderLevel = 10;

    private LocalDate expiryDate;

    private Boolean active;
}
