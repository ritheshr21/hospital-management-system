package com.hms.doctor_service.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentResponse {
    private UUID id;
    private String name;
    private String description;
}
