package com.hms.billing_service.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Local view of the event published by appointment-service. Kept as its own
 * class (rather than a shared jar) so services stay independently deployable;
 * unknown fields are ignored so the producer can evolve without breaking us.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
