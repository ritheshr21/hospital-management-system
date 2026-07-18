package com.hms.pharmacy_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.hms.pharmacy_service.entity.MedicineUnit;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicineResponse {
    private UUID id;
    private String name;
    private String brand;
    private String category;
    private MedicineUnit unit;
    private BigDecimal price;
    private int stockQuantity;
    private int reorderLevel;
    private LocalDate expiryDate;
    private boolean active;
    /** Convenience flag for the pharmacy dashboard. */
    private boolean lowStock;
}
