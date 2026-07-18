package com.hms.pharmacy_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.pharmacy_service.entity.Prescription;
import com.hms.pharmacy_service.entity.PrescriptionStatus;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    List<Prescription> findAllByOrderByCreatedAtDesc();

    List<Prescription> findByStatusOrderByCreatedAtDesc(PrescriptionStatus status);

    List<Prescription> findByAppointmentId(UUID appointmentId);
}
