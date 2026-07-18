package com.hms.appointment_service.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.hms.appointment_service.client.dto.DoctorDto;
import com.hms.appointment_service.client.dto.SlotStatusUpdateDto;

@FeignClient(name = "doctor-service")
public interface DoctorClient {

    @GetMapping("/doctors/{id}")
    DoctorDto getDoctor(@PathVariable UUID id);

    @PatchMapping("/slots/{slotId}")
    void updateSlotStatus(@PathVariable UUID slotId, @RequestBody SlotStatusUpdateDto request);
}
