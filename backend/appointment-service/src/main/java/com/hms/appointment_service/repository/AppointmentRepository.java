package com.hms.appointment_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.appointment_service.entity.Appointment;
import com.hms.appointment_service.entity.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    boolean existsBySlotIdAndStatus(UUID slotId, AppointmentStatus status);

    List<Appointment> findAllByOrderByCreatedAtDesc();

    List<Appointment> findByStatusOrderByCreatedAtDesc(AppointmentStatus status);
}
