package com.hms.doctor_service.mapper;

import com.hms.doctor_service.dto.DepartmentRequest;
import com.hms.doctor_service.dto.DepartmentResponse;
import com.hms.doctor_service.entity.Department;

public final class DepartmentMapper {

    private DepartmentMapper() {
    }

    public static Department toEntity(DepartmentRequest req) {
        return Department.builder()
                .name(req.getName())
                .description(req.getDescription())
                .build();
    }

    public static void updateEntity(Department dept, DepartmentRequest req) {
        dept.setName(req.getName());
        dept.setDescription(req.getDescription());
    }

    public static DepartmentResponse toResponse(Department dept) {
        if (dept == null) {
            return null;
        }
        return DepartmentResponse.builder()
                .id(dept.getId())
                .name(dept.getName())
                .description(dept.getDescription())
                .build();
    }
}
