package com.hms.appointment_service.controller;

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

import com.hms.appointment_service.dto.AppointmentResponse;
import com.hms.appointment_service.dto.BookAppointmentRequest;
import com.hms.appointment_service.dto.QueueEntryResponse;
import com.hms.appointment_service.entity.AppointmentStatus;
import com.hms.appointment_service.service.AppointmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse book(@Valid @RequestBody BookAppointmentRequest request) {
        return appointmentService.book(request);
    }

    @GetMapping
    public List<AppointmentResponse> list(@RequestParam(required = false) AppointmentStatus status) {
        return appointmentService.list(status);
    }

    /** Live waiting list, ordered by the Redis priority queue. */
    @GetMapping("/queue")
    public List<QueueEntryResponse> queue() {
        return appointmentService.queue();
    }

    @GetMapping("/{id}")
    public AppointmentResponse getById(@PathVariable UUID id) {
        return appointmentService.getById(id);
    }

    @PostMapping("/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable UUID id) {
        return appointmentService.cancel(id);
    }

    @PostMapping("/{id}/complete")
    public AppointmentResponse complete(@PathVariable UUID id) {
        return appointmentService.complete(id);
    }
}
