package com.hms.appointment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hms.appointment_service.client.dto.TriageRequestDto;
import com.hms.appointment_service.client.dto.TriageResultDto;

@FeignClient(name = "triage-service")
public interface TriageClient {

    @PostMapping("/triage")
    TriageResultDto triage(@RequestBody TriageRequestDto request);
}
