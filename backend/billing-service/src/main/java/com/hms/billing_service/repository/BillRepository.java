package com.hms.billing_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.billing_service.entity.Bill;
import com.hms.billing_service.entity.BillStatus;

public interface BillRepository extends JpaRepository<Bill, UUID> {

    Optional<Bill> findByAppointmentId(UUID appointmentId);

    boolean existsByAppointmentId(UUID appointmentId);

    List<Bill> findAllByOrderByCreatedAtDesc();

    List<Bill> findByStatusOrderByCreatedAtDesc(BillStatus status);

    long countByInvoiceNumberIsNotNull();
}
