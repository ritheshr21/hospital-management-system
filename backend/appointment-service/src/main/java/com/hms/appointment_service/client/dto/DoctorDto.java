package com.hms.appointment_service.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class DoctorDto {
    private UUID id;
    private String name;
    private boolean active;
    private BigDecimal consultationFee;
    private DepartmentDto department;

    @Data
    public static class DepartmentDto {
        private UUID id;
        private String name;
    }
}
