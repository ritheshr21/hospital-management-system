package com.hms.billing_service.controller;

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

import com.hms.billing_service.dto.BillResponse;
import com.hms.billing_service.dto.CreateBillRequest;
import com.hms.billing_service.dto.PaymentRequest;
import com.hms.billing_service.entity.BillStatus;
import com.hms.billing_service.service.BillService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BillResponse create(@Valid @RequestBody CreateBillRequest request) {
        return billService.create(request);
    }

    @GetMapping
    public List<BillResponse> list(@RequestParam(required = false) BillStatus status) {
        return billService.list(status);
    }

    @GetMapping("/{id}")
    public BillResponse getById(@PathVariable UUID id) {
        return billService.getById(id);
    }

    @GetMapping("/appointment/{appointmentId}")
    public BillResponse getByAppointment(@PathVariable UUID appointmentId) {
        return billService.getByAppointment(appointmentId);
    }

    @PostMapping("/{id}/pay")
    public BillResponse pay(@PathVariable UUID id, @Valid @RequestBody PaymentRequest request) {
        return billService.pay(id, request);
    }

    @PostMapping("/{id}/cancel")
    public BillResponse cancel(@PathVariable UUID id) {
        return billService.cancel(id);
    }
}
