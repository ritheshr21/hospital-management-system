package com.hms.appointment_service.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID patientId;

    @Column(nullable = false)
    private String patientName;

    @Column(nullable = false)
    private UUID doctorId;

    private String doctorName;

    /** Department the appointment is booked under (the doctor's department). */
    private String department;

    /** Captured at booking time so billing can charge the fee that applied then. */
    private BigDecimal consultationFee;

    /** The availability slot reserved in doctor-service. */
    private UUID slotId;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @Column(length = 2000)
    private String symptoms;

    // --- AI triage result ---
    private int urgency;
    private String urgencyLabel;
    private String triageDepartment;
    @Column(length = 1000)
    private String triageReason;
    @Column(length = 1000)
    private String recommendedAction;

    private int tokenNumber;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = AppointmentStatus.BOOKED;
        }
    }
}
