package com.hms.appointment_service.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Published to the "appointment_completed" topic when a consultation finishes.
 * billing-service uses it to raise the bill; notification-service to alert the patient.
 * Kept self-contained so consumers never have to call back into this service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCompletedEvent {
    private UUID appointmentId;
    private UUID patientId;
    private String patientName;
    private UUID doctorId;
    private String doctorName;
    private String department;
    private BigDecimal consultationFee;
    private int tokenNumber;
    private Instant completedAt;
}
