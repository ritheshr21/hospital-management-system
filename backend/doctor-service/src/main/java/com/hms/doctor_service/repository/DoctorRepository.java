package com.hms.doctor_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hms.doctor_service.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    boolean existsByEmail(String email);

    List<Doctor> findByDepartmentId(UUID departmentId);

    long countByDepartmentId(UUID departmentId);

    @Query("""
            SELECT d FROM Doctor d
            WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(d.email) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(d.specialization) LIKE LOWER(CONCAT('%', :q, '%'))
            """)
    List<Doctor> search(@Param("q") String query);
}
