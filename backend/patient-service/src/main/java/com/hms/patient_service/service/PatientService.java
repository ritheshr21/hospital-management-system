package com.hms.patient_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hms.patient_service.dto.PatientRequest;
import com.hms.patient_service.dto.PatientResponse;
import com.hms.patient_service.entity.Patient;
import com.hms.patient_service.exception.EmailAlreadyExistsException;
import com.hms.patient_service.exception.PatientNotFoundException;
import com.hms.patient_service.mapper.PatientMapper;
import com.hms.patient_service.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientResponse create(PatientRequest request) {
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with this email already exists");
        }
        Patient saved = patientRepository.save(PatientMapper.toEntity(request));
        return PatientMapper.toResponse(saved);
    }

    public PatientResponse update(UUID id, PatientRequest request) {
        Patient patient = findEntity(id);

        boolean emailChanged = !patient.getEmail().equalsIgnoreCase(request.getEmail());
        if (emailChanged && patientRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with this email already exists");
        }

        PatientMapper.updateEntity(patient, request);
        return PatientMapper.toResponse(patientRepository.save(patient));
    }

    public PatientResponse getById(UUID id) {
        return PatientMapper.toResponse(findEntity(id));
    }

    public List<PatientResponse> getAll() {
        return patientRepository.findAll().stream()
                .map(PatientMapper::toResponse)
                .toList();
    }

    public List<PatientResponse> search(String query) {
        if (query == null || query.isBlank()) {
            return getAll();
        }
        return patientRepository.search(query.trim()).stream()
                .map(PatientMapper::toResponse)
                .toList();
    }

    public void delete(UUID id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient not found: " + id);
        }
        patientRepository.deleteById(id);
    }

    private Patient findEntity(UUID id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found: " + id));
    }
}
