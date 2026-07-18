package com.hms.pharmacy_service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(StockEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${hms.topics.stock-low}")
    private String stockLowTopic;

    /** Alerting must never break dispensing, so failures are only logged. */
    public void publishStockLow(StockLowEvent event) {
        try {
            kafkaTemplate.send(stockLowTopic, event.getMedicineId().toString(), event);
            log.warn("LOW STOCK: {} at {} (reorder level {})",
                    event.getMedicineName(), event.getStockQuantity(), event.getReorderLevel());
        } catch (Exception e) {
            log.error("Failed to publish stock_low for {}", event.getMedicineId(), e);
        }
    }
}
