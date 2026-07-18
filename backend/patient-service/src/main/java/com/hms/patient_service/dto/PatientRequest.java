package com.hms.patient_service.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.hms.patient_service.entity.Gender;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PatientRequest {

    private UUID userId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String phone;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String bloodGroup;

    private String address;

    @Valid
    private EmergencyContactDto emergencyContact;

    @Valid
    private List<MedicalRecordDto> medicalHistory;
}
