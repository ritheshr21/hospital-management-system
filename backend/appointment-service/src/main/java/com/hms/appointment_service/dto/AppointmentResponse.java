package com.hms.appointment_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hms.appointment_service.entity.AppointmentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppointmentResponse {
    private UUID id;
    private UUID patientId;
    private String patientName;
    private UUID doctorId;
    private String doctorName;
    private String department;
    private BigDecimal consultationFee;
    private UUID slotId;
    private LocalDate date;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private String symptoms;
    private int urgency;
    private String urgencyLabel;
    private String triageDepartment;
    private String triageReason;
    private String recommendedAction;
    private int tokenNumber;
    private AppointmentStatus status;
    private Instant createdAt;
}
