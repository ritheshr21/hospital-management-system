package com.hms.pharmacy_service.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.hms.pharmacy_service.entity.PrescriptionStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrescriptionResponse {
    private UUID id;
    private UUID appointmentId;
    private UUID patientId;
    private String patientName;
    private UUID doctorId;
    private String doctorName;
    private String notes;
    private List<PrescriptionItemDto> items;
    private PrescriptionStatus status;
    private Instant createdAt;
    private Instant dispensedAt;
}
