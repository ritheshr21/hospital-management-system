package com.hms.doctor_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hms.doctor_service.dto.AvailabilitySlotRequest;
import com.hms.doctor_service.dto.AvailabilitySlotResponse;
import com.hms.doctor_service.dto.SlotStatusUpdateRequest;
import com.hms.doctor_service.entity.AvailabilitySlot;
import com.hms.doctor_service.exception.InvalidSlotException;
import com.hms.doctor_service.exception.ResourceNotFoundException;
import com.hms.doctor_service.mapper.SlotMapper;
import com.hms.doctor_service.repository.AvailabilitySlotRepository;
import com.hms.doctor_service.repository.DoctorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilitySlotRepository slotRepository;
    private final DoctorRepository doctorRepository;

    public AvailabilitySlotResponse addSlot(UUID doctorId, AvailabilitySlotRequest request) {
        requireDoctor(doctorId);

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InvalidSlotException("End time must be after start time");
        }

        boolean overlaps = slotRepository
                .findByDoctorIdAndDateOrderByStartTimeAsc(doctorId, request.getDate())
                .stream()
                .anyMatch(s -> request.getStartTime().isBefore(s.getEndTime())
                        && s.getStartTime().isBefore(request.getEndTime()));
        if (overlaps) {
            throw new InvalidSlotException("This slot overlaps an existing one for the day");
        }

        AvailabilitySlot saved = slotRepository.save(SlotMapper.toEntity(doctorId, request));
        return SlotMapper.toResponse(saved);
    }

    public List<AvailabilitySlotResponse> getSlots(UUID doctorId, LocalDate date) {
        requireDoctor(doctorId);
        List<AvailabilitySlot> slots = (date != null)
                ? slotRepository.findByDoctorIdAndDateOrderByStartTimeAsc(doctorId, date)
                : slotRepository.findByDoctorIdOrderByDateAscStartTimeAsc(doctorId);
        return slots.stream().map(SlotMapper::toResponse).toList();
    }

    public AvailabilitySlotResponse updateStatus(UUID slotId, SlotStatusUpdateRequest request) {
        AvailabilitySlot slot = findSlot(slotId);
        slot.setStatus(request.getStatus());
        return SlotMapper.toResponse(slotRepository.save(slot));
    }

    public void deleteSlot(UUID slotId) {
        slotRepository.delete(findSlot(slotId));
    }

    private void requireDoctor(UUID doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found: " + doctorId);
        }
    }

    private AvailabilitySlot findSlot(UUID slotId) {
        return slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found: " + slotId));
    }
}
