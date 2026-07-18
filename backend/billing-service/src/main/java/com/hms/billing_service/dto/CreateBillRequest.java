package com.hms.billing_service.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateBillRequest {

    private UUID appointmentId;

    private UUID patientId;

    @NotBlank(message = "Patient name is required")
    private String patientName;

    private String doctorName;

    private String department;

    @NotEmpty(message = "At least one bill item is required")
    @Valid
    private List<BillItemDto> items;
}
