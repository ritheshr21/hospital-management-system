package com.hms.doctor_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hms.doctor_service.dto.DepartmentRequest;
import com.hms.doctor_service.dto.DepartmentResponse;
import com.hms.doctor_service.entity.Department;
import com.hms.doctor_service.exception.DuplicateResourceException;
import com.hms.doctor_service.exception.ResourceNotFoundException;
import com.hms.doctor_service.mapper.DepartmentMapper;
import com.hms.doctor_service.repository.DepartmentRepository;
import com.hms.doctor_service.repository.DoctorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;

    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("A department with this name already exists");
        }
        return DepartmentMapper.toResponse(departmentRepository.save(DepartmentMapper.toEntity(request)));
    }

    public DepartmentResponse update(UUID id, DepartmentRequest request) {
        Department dept = findEntity(id);
        boolean nameChanged = !dept.getName().equalsIgnoreCase(request.getName());
        if (nameChanged && departmentRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("A department with this name already exists");
        }
        DepartmentMapper.updateEntity(dept, request);
        return DepartmentMapper.toResponse(departmentRepository.save(dept));
    }

    public DepartmentResponse getById(UUID id) {
        return DepartmentMapper.toResponse(findEntity(id));
    }

    public List<DepartmentResponse> getAll() {
        return departmentRepository.findAll().stream()
                .map(DepartmentMapper::toResponse)
                .toList();
    }

    public void delete(UUID id) {
        Department dept = findEntity(id);
        if (doctorRepository.countByDepartmentId(id) > 0) {
            throw new DuplicateResourceException("Cannot delete a department that still has doctors assigned");
        }
        departmentRepository.delete(dept);
    }

    private Department findEntity(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }
}
