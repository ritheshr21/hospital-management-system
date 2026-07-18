package com.hms.doctor_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DoctorRequest {

    private UUID userId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String phone;

    private String specialization;

    private String qualification;

    private BigDecimal consultationFee;

    private Boolean active;

    /** Department to assign the doctor to (optional). */
    private UUID departmentId;
}
