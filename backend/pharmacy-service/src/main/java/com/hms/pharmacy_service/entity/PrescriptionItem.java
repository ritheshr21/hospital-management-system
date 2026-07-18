package com.hms.pharmacy_service.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prescription_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItem {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID medicineId;

    /** Snapshot of the name so historical prescriptions stay readable. */
    private String medicineName;

    /** e.g. "1-0-1" (morning-afternoon-night). */
    private String dosage;

    private int durationDays;

    /** Total units to dispense. */
    private int quantity;

    private String instructions;
}
