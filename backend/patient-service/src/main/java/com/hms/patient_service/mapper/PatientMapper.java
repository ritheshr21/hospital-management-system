package com.hms.patient_service.mapper;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.hms.patient_service.dto.EmergencyContactDto;
import com.hms.patient_service.dto.MedicalRecordDto;
import com.hms.patient_service.dto.PatientRequest;
import com.hms.patient_service.dto.PatientResponse;
import com.hms.patient_service.entity.EmergencyContact;
import com.hms.patient_service.entity.MedicalRecord;
import com.hms.patient_service.entity.Patient;

/**
 * Translates between Patient entities and their request/response DTOs.
 * Keeping this out of the service keeps the service focused on behaviour.
 */
public final class PatientMapper {

    private PatientMapper() {
    }

    public static Patient toEntity(PatientRequest req) {
        Patient patient = Patient.builder()
                .userId(req.getUserId())
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .gender(req.getGender())
                .dateOfBirth(req.getDateOfBirth())
                .bloodGroup(req.getBloodGroup())
                .address(req.getAddress())
                .emergencyContact(toEmergencyContact(req.getEmergencyContact()))
                .build();
        patient.setMedicalHistory(toMedicalRecords(req.getMedicalHistory()));
        return patient;
    }

    /** Copies editable fields from the request onto an existing entity. */
    public static void updateEntity(Patient patient, PatientRequest req) {
        patient.setName(req.getName());
        patient.setEmail(req.getEmail());
        patient.setPhone(req.getPhone());
        patient.setGender(req.getGender());
        patient.setDateOfBirth(req.getDateOfBirth());
        patient.setBloodGroup(req.getBloodGroup());
        patient.setAddress(req.getAddress());
        patient.setEmergencyContact(toEmergencyContact(req.getEmergencyContact()));
        // Replace medical history in place so orphanRemoval cleans up old rows.
        patient.getMedicalHistory().clear();
        patient.getMedicalHistory().addAll(toMedicalRecords(req.getMedicalHistory()));
    }

    public static PatientResponse toResponse(Patient p) {
        return PatientResponse.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .name(p.getName())
                .email(p.getEmail())
                .phone(p.getPhone())
                .gender(p.getGender())
                .dateOfBirth(p.getDateOfBirth())
                .bloodGroup(p.getBloodGroup())
                .address(p.getAddress())
                .emergencyContact(toEmergencyContactDto(p.getEmergencyContact()))
                .medicalHistory(p.getMedicalHistory().stream()
                        .map(PatientMapper::toMedicalRecordDto)
                        .collect(Collectors.toList()))
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private static EmergencyContact toEmergencyContact(EmergencyContactDto dto) {
        if (dto == null) {
            return null;
        }
        return EmergencyContact.builder()
                .contactName(dto.getContactName())
                .relationship(dto.getRelationship())
                .contactPhone(dto.getContactPhone())
                .build();
    }

    private static EmergencyContactDto toEmergencyContactDto(EmergencyContact c) {
        if (c == null) {
            return null;
        }
        EmergencyContactDto dto = new EmergencyContactDto();
        dto.setContactName(c.getContactName());
        dto.setRelationship(c.getRelationship());
        dto.setContactPhone(c.getContactPhone());
        return dto;
    }

    private static List<MedicalRecord> toMedicalRecords(List<MedicalRecordDto> dtos) {
        if (dtos == null) {
            return new java.util.ArrayList<>();
        }
        return dtos.stream()
                .map(d -> MedicalRecord.builder()
                        .condition(d.getCondition())
                        .notes(d.getNotes())
                        .recordedAt(d.getRecordedAt() != null ? d.getRecordedAt() : Instant.now())
                        .build())
                .collect(Collectors.toCollection(java.util.ArrayList::new));
    }

    private static MedicalRecordDto toMedicalRecordDto(MedicalRecord r) {
        MedicalRecordDto dto = new MedicalRecordDto();
        dto.setId(r.getId());
        dto.setCondition(r.getCondition());
        dto.setNotes(r.getNotes());
        dto.setRecordedAt(r.getRecordedAt());
        return dto;
    }
}
