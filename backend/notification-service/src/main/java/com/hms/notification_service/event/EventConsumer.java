package com.hms.notification_service.event;

import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.notification_service.entity.Notification;
import com.hms.notification_service.entity.NotificationType;
import com.hms.notification_service.service.NotificationService;

import lombok.RequiredArgsConstructor;

/**
 * The observer side of the system: reacts to domain events from other services
 * without them knowing this service exists.
 */
@Component
@RequiredArgsConstructor
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${hms.topics.appointment-completed}", groupId = "notification-service")
    public void onAppointmentCompleted(String payload) {
        try {
            JsonNode e = objectMapper.readTree(payload);
            UUID appointmentId = uuid(e, "appointmentId");
            if (notificationService.alreadyRecorded(appointmentId, NotificationType.APPOINTMENT_COMPLETED)) {
                return;
            }

            String patient = e.path("patientName").asText("Patient");
            String doctor = e.path("doctorName").asText("your doctor");

            notificationService.record(Notification.builder()
                    .type(NotificationType.APPOINTMENT_COMPLETED)
                    .recipientId(uuid(e, "patientId"))
                    .recipientName(patient)
                    .referenceId(appointmentId)
                    .title("Consultation completed")
                    .message("Hi %s, your consultation with %s is complete. Your invoice is ready."
                            .formatted(patient, doctor))
                    .build());
            log.info("Notification created for completed appointment {}", appointmentId);
        } catch (Exception ex) {
            log.error("Failed to handle appointment_completed: {}", payload, ex);
        }
    }

    @KafkaListener(topics = "${hms.topics.bill-paid}", groupId = "notification-service")
    public void onBillPaid(String payload) {
        try {
            JsonNode e = objectMapper.readTree(payload);
            UUID billId = uuid(e, "billId");
            if (notificationService.alreadyRecorded(billId, NotificationType.BILL_PAID)) {
                return;
            }

            String patient = e.path("patientName").asText("Patient");
            String invoice = e.path("invoiceNumber").asText("");
            BigDecimal total = e.path("total").decimalValue();
            String method = e.path("paymentMethod").asText("");

            notificationService.record(Notification.builder()
                    .type(NotificationType.BILL_PAID)
                    .recipientId(uuid(e, "patientId"))
                    .recipientName(patient)
                    .referenceId(billId)
                    .title("Payment received - " + invoice)
                    .message("Hi %s, we received your payment of %s via %s. Invoice %s is settled. Thank you."
                            .formatted(patient, total, method, invoice))
                    .build());
            log.info("Notification created for paid bill {}", invoice);
        } catch (Exception ex) {
            log.error("Failed to handle bill_paid: {}", payload, ex);
        }
    }

    /**
     * Inventory alert. Deliberately NOT deduped by referenceId: every time stock
     * drops further the pharmacy should be told again.
     */
    @KafkaListener(topics = "${hms.topics.stock-low}", groupId = "notification-service")
    public void onStockLow(String payload) {
        try {
            JsonNode e = objectMapper.readTree(payload);
            String medicine = e.path("medicineName").asText("A medicine");
            int stock = e.path("stockQuantity").asInt();
            int reorder = e.path("reorderLevel").asInt();

            notificationService.record(Notification.builder()
                    .type(NotificationType.STOCK_LOW)
                    .recipientName("Pharmacy")
                    .referenceId(uuid(e, "medicineId"))
                    .title("Low stock: " + medicine)
                    .message("%s is down to %d units (reorder level %d). Restock soon."
                            .formatted(medicine, stock, reorder))
                    .build());
            log.info("Low-stock notification created for {}", medicine);
        } catch (Exception ex) {
            log.error("Failed to handle stock_low: {}", payload, ex);
        }
    }

    private UUID uuid(JsonNode node, String field) {
        String value = node.path(field).asText(null);
        return (value == null || value.isBlank() || "null".equals(value)) ? null : UUID.fromString(value);
    }
}
