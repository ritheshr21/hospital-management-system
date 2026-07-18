package com.hms.doctor_service.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.doctor_service.entity.AvailabilitySlot;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, UUID> {

    List<AvailabilitySlot> findByDoctorIdOrderByDateAscStartTimeAsc(UUID doctorId);

    List<AvailabilitySlot> findByDoctorIdAndDateOrderByStartTimeAsc(UUID doctorId, LocalDate date);
}
