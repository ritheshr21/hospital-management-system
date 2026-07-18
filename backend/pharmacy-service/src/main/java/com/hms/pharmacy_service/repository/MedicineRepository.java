package com.hms.pharmacy_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hms.pharmacy_service.entity.Medicine;

public interface MedicineRepository extends JpaRepository<Medicine, UUID> {

    boolean existsByNameIgnoreCase(String name);

    @Query("""
            SELECT m FROM Medicine m
            WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(m.brand) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(m.category) LIKE LOWER(CONCAT('%', :q, '%'))
            """)
    List<Medicine> search(@Param("q") String query);

    /** Stock at or below the reorder level — drives the inventory alerts. */
    @Query("SELECT m FROM Medicine m WHERE m.stockQuantity <= m.reorderLevel ORDER BY m.stockQuantity ASC")
    List<Medicine> findLowStock();
}
