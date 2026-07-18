package com.hms.patient_service.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.hms.patient_service.entity.Gender;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientResponse {

    private UUID id;
    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String bloodGroup;
    private String address;
    private EmergencyContactDto emergencyContact;
    private List<MedicalRecordDto> medicalHistory;
    private Instant createdAt;
    private Instant updatedAt;
}
