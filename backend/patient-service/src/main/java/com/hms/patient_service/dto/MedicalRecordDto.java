package com.hms.patient_service.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class MedicalRecordDto {
    private UUID id;
    private String condition;
    private String notes;
    private Instant recordedAt;
}
