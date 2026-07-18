package com.hms.pharmacy_service.stock;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hms.pharmacy_service.entity.Medicine;
import com.hms.pharmacy_service.entity.PrescriptionItem;
import com.hms.pharmacy_service.event.StockEventPublisher;
import com.hms.pharmacy_service.event.StockLowEvent;
import com.hms.pharmacy_service.exception.InsufficientStockException;
import com.hms.pharmacy_service.exception.ResourceNotFoundException;
import com.hms.pharmacy_service.repository.MedicineRepository;

import lombok.RequiredArgsConstructor;

/**
 * Single owner of every stock movement. Keeping deduct/restock here (rather than
 * scattered across services) means the low-stock alert can never be forgotten,
 * and there is one place to reason about availability.
 */
@Component
@RequiredArgsConstructor
public class StockManager {

    private static final Logger log = LoggerFactory.getLogger(StockManager.class);

    private final MedicineRepository medicineRepository;
    private final StockEventPublisher eventPublisher;

    /**
     * Checks every item can be fulfilled BEFORE deducting any, so a prescription
     * is never half-dispensed.
     */
    public void assertAvailable(List<PrescriptionItem> items) {
        for (PrescriptionItem item : items) {
            Medicine medicine = require(item.getMedicineId());
            if (medicine.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for %s: need %d, have %d"
                                .formatted(medicine.getName(), item.getQuantity(), medicine.getStockQuantity()));
            }
        }
    }

    /** Deducts stock for all items and raises an alert for any that fall low. */
    public void deduct(List<PrescriptionItem> items) {
        for (PrescriptionItem item : items) {
            Medicine medicine = require(item.getMedicineId());

            // Re-check inside the transaction; @Version guards concurrent dispensing.
            if (medicine.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for %s: need %d, have %d"
                                .formatted(medicine.getName(), item.getQuantity(), medicine.getStockQuantity()));
            }

            medicine.setStockQuantity(medicine.getStockQuantity() - item.getQuantity());
            medicineRepository.save(medicine);
            log.info("Dispensed {} x{} (stock now {})",
                    medicine.getName(), item.getQuantity(), medicine.getStockQuantity());

            alertIfLow(medicine);
        }
    }

    public Medicine restock(Medicine medicine, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive");
        }
        medicine.setStockQuantity(medicine.getStockQuantity() + quantity);
        return medicineRepository.save(medicine);
    }

    public List<Medicine> lowStock() {
        return medicineRepository.findLowStock();
    }

    private void alertIfLow(Medicine medicine) {
        if (medicine.isLowStock()) {
            eventPublisher.publishStockLow(StockLowEvent.builder()
                    .medicineId(medicine.getId())
                    .medicineName(medicine.getName())
                    .stockQuantity(medicine.getStockQuantity())
                    .reorderLevel(medicine.getReorderLevel())
                    .detectedAt(Instant.now())
                    .build());
        }
    }

    private Medicine require(java.util.UUID medicineId) {
        return medicineRepository.findById(medicineId)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + medicineId));
    }
}
