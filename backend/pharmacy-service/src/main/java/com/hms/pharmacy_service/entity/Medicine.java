package com.hms.pharmacy_service.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "medicines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    private String brand;

    private String category;

    @Enumerated(EnumType.STRING)
    private MedicineUnit unit;

    private BigDecimal price;

    @Builder.Default
    private int stockQuantity = 0;

    /** Stock at or below this triggers a low-stock alert. */
    @Builder.Default
    private int reorderLevel = 10;

    private LocalDate expiryDate;

    @Builder.Default
    private boolean active = true;

    /**
     * Optimistic lock: two pharmacists dispensing the same medicine at once
     * must not both succeed off a stale stock count.
     */
    @Version
    private Long version;

    private Instant createdAt;
    private Instant updatedAt;

    public boolean isLowStock() {
        return stockQuantity <= reorderLevel;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
