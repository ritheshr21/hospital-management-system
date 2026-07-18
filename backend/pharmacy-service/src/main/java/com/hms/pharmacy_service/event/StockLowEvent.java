package com.hms.pharmacy_service.event;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Published to "stock_low" when a medicine falls to or below its reorder level. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockLowEvent {
    private UUID medicineId;
    private String medicineName;
    private int stockQuantity;
    private int reorderLevel;
    private Instant detectedAt;
}
