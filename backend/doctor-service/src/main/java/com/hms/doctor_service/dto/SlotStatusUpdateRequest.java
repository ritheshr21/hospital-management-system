package com.hms.doctor_service.dto;

import com.hms.doctor_service.entity.SlotStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SlotStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private SlotStatus status;
}
