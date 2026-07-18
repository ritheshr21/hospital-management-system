package com.hms.doctor_service.mapper;

import java.util.UUID;

import com.hms.doctor_service.dto.AvailabilitySlotRequest;
import com.hms.doctor_service.dto.AvailabilitySlotResponse;
import com.hms.doctor_service.entity.AvailabilitySlot;
import com.hms.doctor_service.entity.SlotStatus;

public final class SlotMapper {

    private SlotMapper() {
    }

    public static AvailabilitySlot toEntity(UUID doctorId, AvailabilitySlotRequest req) {
        return AvailabilitySlot.builder()
                .doctorId(doctorId)
                .date(req.getDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(req.getStatus() == null ? SlotStatus.AVAILABLE : req.getStatus())
                .build();
    }

    public static AvailabilitySlotResponse toResponse(AvailabilitySlot s) {
        return AvailabilitySlotResponse.builder()
                .id(s.getId())
                .doctorId(s.getDoctorId())
                .date(s.getDate())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .status(s.getStatus())
                .build();
    }
}
