package com.hms.billing_service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BillEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(BillEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${hms.topics.bill-paid}")
    private String billPaidTopic;

    /** Never let a notification failure roll back a successful payment. */
    public void publishPaid(BillPaidEvent event) {
        try {
            kafkaTemplate.send(billPaidTopic, event.getBillId().toString(), event);
            log.info("Published bill_paid for invoice {}", event.getInvoiceNumber());
        } catch (Exception e) {
            log.error("Failed to publish bill_paid for {}", event.getBillId(), e);
        }
    }
}
