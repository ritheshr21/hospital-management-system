package com.hms.doctor_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorResponse {
    private UUID id;
    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private String specialization;
    private String qualification;
    private BigDecimal consultationFee;
    private boolean active;
    private DepartmentResponse department;
    private Instant createdAt;
    private Instant updatedAt;
}
