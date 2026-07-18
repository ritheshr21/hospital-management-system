package com.hms.doctor_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.doctor_service.dto.DoctorRequest;
import com.hms.doctor_service.dto.DoctorResponse;
import com.hms.doctor_service.entity.Department;
import com.hms.doctor_service.entity.Doctor;
import com.hms.doctor_service.exception.DuplicateResourceException;
import com.hms.doctor_service.exception.ResourceNotFoundException;
import com.hms.doctor_service.mapper.DoctorMapper;
import com.hms.doctor_service.repository.AvailabilitySlotRepository;
import com.hms.doctor_service.repository.DepartmentRepository;
import com.hms.doctor_service.repository.DoctorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final AvailabilitySlotRepository slotRepository;

    public DoctorResponse create(DoctorRequest request) {
        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("A doctor with this email already exists");
        }
        Department department = resolveDepartment(request.getDepartmentId());
        Doctor saved = doctorRepository.save(DoctorMapper.toEntity(request, department));
        return DoctorMapper.toResponse(saved);
    }

    public DoctorResponse update(UUID id, DoctorRequest request) {
        Doctor doctor = findEntity(id);
        boolean emailChanged = !doctor.getEmail().equalsIgnoreCase(request.getEmail());
        if (emailChanged && doctorRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("A doctor with this email already exists");
        }
        Department department = resolveDepartment(request.getDepartmentId());
        DoctorMapper.updateEntity(doctor, request, department);
        return DoctorMapper.toResponse(doctorRepository.save(doctor));
    }

    public DoctorResponse getById(UUID id) {
        return DoctorMapper.toResponse(findEntity(id));
    }

    public List<DoctorResponse> list(String search, UUID departmentId) {
        List<Doctor> doctors;
        if (departmentId != null) {
            doctors = doctorRepository.findByDepartmentId(departmentId);
        } else if (search != null && !search.isBlank()) {
            doctors = doctorRepository.search(search.trim());
        } else {
            doctors = doctorRepository.findAll();
        }
        return doctors.stream().map(DoctorMapper::toResponse).toList();
    }

    @Transactional
    public void delete(UUID id) {
        Doctor doctor = findEntity(id);
        // Clean up this doctor's availability so no orphan slots are left behind.
        slotRepository.deleteAll(slotRepository.findByDoctorIdOrderByDateAscStartTimeAsc(id));
        doctorRepository.delete(doctor);
    }

    private Department resolveDepartment(UUID departmentId) {
        if (departmentId == null) {
            return null;
        }
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + departmentId));
    }

    private Doctor findEntity(UUID id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));
    }
}
