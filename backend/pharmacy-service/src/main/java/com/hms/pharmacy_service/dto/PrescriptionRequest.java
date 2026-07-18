package com.hms.pharmacy_service.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PrescriptionRequest {

    private UUID appointmentId;

    private UUID patientId;

    @NotBlank(message = "Patient name is required")
    private String patientName;

    private UUID doctorId;

    private String doctorName;

    private String notes;

    @NotEmpty(message = "At least one medicine is required")
    @Valid
    private List<PrescriptionItemDto> items;
}
