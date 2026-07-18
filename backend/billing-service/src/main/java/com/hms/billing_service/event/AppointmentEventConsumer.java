package com.hms.billing_service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.billing_service.service.BillService;

import lombok.RequiredArgsConstructor;

/**
 * Raises the bill automatically when a consultation is completed.
 */
@Component
@RequiredArgsConstructor
public class AppointmentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(AppointmentEventConsumer.class);

    private final BillService billService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${hms.topics.appointment-completed}", groupId = "billing-service")
    public void onAppointmentCompleted(String payload) {
        try {
            AppointmentCompletedEvent event = objectMapper.readValue(payload, AppointmentCompletedEvent.class);
            log.info("Received appointment_completed for appointment {}", event.getAppointmentId());
            billService.createFromAppointment(event);
        } catch (Exception e) {
            // Swallow so one bad message doesn't block the partition forever.
            log.error("Failed to handle appointment_completed payload: {}", payload, e);
        }
    }
}
