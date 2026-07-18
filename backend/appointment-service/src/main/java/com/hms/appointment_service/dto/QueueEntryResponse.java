package com.hms.appointment_service.dto;

import lombok.Builder;
import lombok.Data;

/**
 * One row in the live waiting list — an appointment plus its current position
 * in the priority queue (1 = seen next).
 */
@Data
@Builder
public class QueueEntryResponse {
    private int position;
    private AppointmentResponse appointment;
}
