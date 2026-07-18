package com.hms.triage_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.triage_service.dto.TriageRequest;
import com.hms.triage_service.dto.TriageResponse;
import com.hms.triage_service.service.TriageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/triage")
@RequiredArgsConstructor
public class TriageController {

    private final TriageService triageService;

    @PostMapping
    public TriageResponse triage(@Valid @RequestBody TriageRequest request) {
        return triageService.triage(request);
    }
}
