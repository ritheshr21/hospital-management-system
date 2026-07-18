package com.hms.doctor_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hms.doctor_service.entity.SlotStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AvailabilitySlotRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    /** Optional; defaults to AVAILABLE when creating. */
    private SlotStatus status;
}
