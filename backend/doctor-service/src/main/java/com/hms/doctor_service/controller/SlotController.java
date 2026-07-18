package com.hms.doctor_service.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.doctor_service.dto.AvailabilitySlotResponse;
import com.hms.doctor_service.dto.SlotStatusUpdateRequest;
import com.hms.doctor_service.service.AvailabilityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Operations on an individual slot by its own id — used to change availability
 * (e.g. BLOCK a slot) or remove it.
 */
@RestController
@RequestMapping("/slots")
@RequiredArgsConstructor
public class SlotController {

    private final AvailabilityService availabilityService;

    @PatchMapping("/{slotId}")
    public AvailabilitySlotResponse updateStatus(@PathVariable UUID slotId,
            @Valid @RequestBody SlotStatusUpdateRequest request) {
        return availabilityService.updateStatus(slotId, request);
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> delete(@PathVariable UUID slotId) {
        availabilityService.deleteSlot(slotId);
        return ResponseEntity.noContent().build();
    }
}
