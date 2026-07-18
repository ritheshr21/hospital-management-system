package com.hms.doctor_service.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hms.doctor_service.dto.AvailabilitySlotRequest;
import com.hms.doctor_service.dto.AvailabilitySlotResponse;
import com.hms.doctor_service.dto.DoctorRequest;
import com.hms.doctor_service.dto.DoctorResponse;
import com.hms.doctor_service.service.AvailabilityService;
import com.hms.doctor_service.service.DoctorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final AvailabilityService availabilityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorResponse create(@Valid @RequestBody DoctorRequest request) {
        return doctorService.create(request);
    }

    @GetMapping
    public List<DoctorResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID departmentId) {
        return doctorService.list(search, departmentId);
    }

    @GetMapping("/{id}")
    public DoctorResponse getById(@PathVariable UUID id) {
        return doctorService.getById(id);
    }

    @PutMapping("/{id}")
    public DoctorResponse update(@PathVariable UUID id, @Valid @RequestBody DoctorRequest request) {
        return doctorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Availability slots for a doctor ---

    @PostMapping("/{id}/slots")
    @ResponseStatus(HttpStatus.CREATED)
    public AvailabilitySlotResponse addSlot(@PathVariable UUID id, @Valid @RequestBody AvailabilitySlotRequest request) {
        return availabilityService.addSlot(id, request);
    }

    @GetMapping("/{id}/slots")
    public List<AvailabilitySlotResponse> getSlots(
            @PathVariable UUID id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return availabilityService.getSlots(id, date);
    }
}
