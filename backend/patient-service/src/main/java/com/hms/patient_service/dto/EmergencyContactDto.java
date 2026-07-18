package com.hms.patient_service.dto;

import lombok.Data;

@Data
public class EmergencyContactDto {
    private String contactName;
    private String relationship;
    private String contactPhone;
}
