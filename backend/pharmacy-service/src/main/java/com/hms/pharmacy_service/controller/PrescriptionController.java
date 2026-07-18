package com.hms.pharmacy_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hms.pharmacy_service.dto.PrescriptionRequest;
import com.hms.pharmacy_service.dto.PrescriptionResponse;
import com.hms.pharmacy_service.entity.PrescriptionStatus;
import com.hms.pharmacy_service.service.PrescriptionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrescriptionResponse create(@Valid @RequestBody PrescriptionRequest request) {
        return prescriptionService.create(request);
    }

    @GetMapping
    public List<PrescriptionResponse> list(@RequestParam(required = false) PrescriptionStatus status) {
        return prescriptionService.list(status);
    }

    @GetMapping("/{id}")
    public PrescriptionResponse getById(@PathVariable UUID id) {
        return prescriptionService.getById(id);
    }

    @PostMapping("/{id}/dispense")
    public PrescriptionResponse dispense(@PathVariable UUID id) {
        return prescriptionService.dispense(id);
    }

    @PostMapping("/{id}/cancel")
    public PrescriptionResponse cancel(@PathVariable UUID id) {
        return prescriptionService.cancel(id);
    }
}
