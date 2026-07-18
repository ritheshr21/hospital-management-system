package com.hms.doctor_service.mapper;

import com.hms.doctor_service.dto.DoctorRequest;
import com.hms.doctor_service.dto.DoctorResponse;
import com.hms.doctor_service.entity.Department;
import com.hms.doctor_service.entity.Doctor;

public final class DoctorMapper {

    private DoctorMapper() {
    }

    public static Doctor toEntity(DoctorRequest req, Department department) {
        return Doctor.builder()
                .userId(req.getUserId())
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .specialization(req.getSpecialization())
                .qualification(req.getQualification())
                .consultationFee(req.getConsultationFee())
                .active(req.getActive() == null ? true : req.getActive())
                .department(department)
                .build();
    }

    public static void updateEntity(Doctor doctor, DoctorRequest req, Department department) {
        doctor.setName(req.getName());
        doctor.setEmail(req.getEmail());
        doctor.setPhone(req.getPhone());
        doctor.setSpecialization(req.getSpecialization());
        doctor.setQualification(req.getQualification());
        doctor.setConsultationFee(req.getConsultationFee());
        if (req.getActive() != null) {
            doctor.setActive(req.getActive());
        }
        doctor.setDepartment(department);
    }

    public static DoctorResponse toResponse(Doctor d) {
        return DoctorResponse.builder()
                .id(d.getId())
                .userId(d.getUserId())
                .name(d.getName())
                .email(d.getEmail())
                .phone(d.getPhone())
                .specialization(d.getSpecialization())
                .qualification(d.getQualification())
                .consultationFee(d.getConsultationFee())
                .active(d.isActive())
                .department(DepartmentMapper.toResponse(d.getDepartment()))
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
