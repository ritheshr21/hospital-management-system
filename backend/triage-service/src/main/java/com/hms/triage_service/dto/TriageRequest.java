package com.hms.triage_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TriageRequest {

    @NotBlank(message = "Symptoms description is required")
    private String symptoms;

    /** Optional context that improves triage accuracy. */
    private Integer age;

    private String sex;
}
