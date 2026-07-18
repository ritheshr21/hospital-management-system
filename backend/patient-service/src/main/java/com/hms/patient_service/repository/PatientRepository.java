package com.hms.patient_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hms.patient_service.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    boolean existsByEmail(String email);

    @Query("""
            SELECT p FROM Patient p
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(p.email) LIKE LOWER(CONCAT('%', :q, '%'))
               OR p.phone LIKE CONCAT('%', :q, '%')
            """)
    List<Patient> search(@Param("q") String query);
}
