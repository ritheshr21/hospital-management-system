package com.hms.appointment_service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppointmentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AppointmentEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${hms.topics.appointment-completed}")
    private String appointmentCompletedTopic;

    /**
     * Publishing must never break completing an appointment, so failures are
     * logged rather than thrown.
     */
    public void publishCompleted(AppointmentCompletedEvent event) {
        try {
            kafkaTemplate.send(appointmentCompletedTopic, event.getAppointmentId().toString(), event);
            log.info("Published appointment_completed for appointment {}", event.getAppointmentId());
        } catch (Exception e) {
            log.error("Failed to publish appointment_completed for {}", event.getAppointmentId(), e);
        }
    }
}
