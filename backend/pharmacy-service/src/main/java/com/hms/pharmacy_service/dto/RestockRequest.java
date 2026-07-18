package com.hms.pharmacy_service.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RestockRequest {

    @Positive(message = "Quantity must be positive")
    private int quantity;
}
