package com.hms.doctor_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hms.doctor_service.entity.SlotStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvailabilitySlotResponse {
    private UUID id;
    private UUID doctorId;
    private LocalDate date;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private SlotStatus status;
}
