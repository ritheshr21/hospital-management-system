package com.hms.appointment_service.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hms.appointment_service.client.DoctorClient;
import com.hms.appointment_service.client.TriageClient;
import com.hms.appointment_service.client.dto.DoctorDto;
import com.hms.appointment_service.client.dto.SlotStatusUpdateDto;
import com.hms.appointment_service.client.dto.TriageRequestDto;
import com.hms.appointment_service.client.dto.TriageResultDto;
import com.hms.appointment_service.dto.AppointmentResponse;
import com.hms.appointment_service.dto.BookAppointmentRequest;
import com.hms.appointment_service.dto.QueueEntryResponse;
import com.hms.appointment_service.entity.Appointment;
import com.hms.appointment_service.entity.AppointmentStatus;
import com.hms.appointment_service.event.AppointmentCompletedEvent;
import com.hms.appointment_service.event.AppointmentEventPublisher;
import com.hms.appointment_service.exception.BookingConflictException;
import com.hms.appointment_service.exception.ResourceNotFoundException;
import com.hms.appointment_service.exception.ServiceUnavailableException;
import com.hms.appointment_service.mapper.AppointmentMapper;
import com.hms.appointment_service.queue.QueueManager;
import com.hms.appointment_service.repository.AppointmentRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final QueueManager queueManager;
    private final TriageClient triageClient;
    private final DoctorClient doctorClient;
    private final AppointmentEventPublisher eventPublisher;

    public AppointmentResponse book(BookAppointmentRequest request) {
        DoctorDto doctor = fetchDoctor(request.getDoctorId());
        if (!doctor.isActive()) {
            throw new BookingConflictException("This doctor is not currently accepting appointments");
        }

        if (request.getSlotId() != null
                && appointmentRepository.existsBySlotIdAndStatus(request.getSlotId(), AppointmentStatus.BOOKED)) {
            throw new BookingConflictException("This slot is already booked");
        }

        // AI triage — degrade gracefully so booking never hard-fails if triage is down.
        TriageResultDto triage = runTriage(request);

        String department = doctor.getDepartment() != null
                ? doctor.getDepartment().getName()
                : triage.getDepartment();

        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .patientName(request.getPatientName())
                .doctorId(doctor.getId())
                .doctorName(doctor.getName())
                .department(department)
                .consultationFee(doctor.getConsultationFee())
                .slotId(request.getSlotId())
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .symptoms(request.getSymptoms())
                .urgency(triage.getUrgency())
                .urgencyLabel(triage.getUrgencyLabel())
                .triageDepartment(triage.getDepartment())
                .triageReason(triage.getReason())
                .recommendedAction(triage.getRecommendedAction())
                .tokenNumber(queueManager.nextToken())
                .status(AppointmentStatus.BOOKED)
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        // Reserve the slot (best effort — booking is already persisted).
        if (saved.getSlotId() != null) {
            updateSlot(saved.getSlotId(), "BOOKED");
        }

        // Add to the Redis priority queue, ordered by urgency.
        queueManager.enqueue(saved.getId(), saved.getUrgency());

        return AppointmentMapper.toResponse(saved);
    }

    public AppointmentResponse cancel(UUID id) {
        Appointment appointment = findEntity(id);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BookingConflictException("Appointment is already cancelled");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        queueManager.remove(appointment.getId());
        if (appointment.getSlotId() != null) {
            updateSlot(appointment.getSlotId(), "AVAILABLE");
        }
        return AppointmentMapper.toResponse(appointment);
    }

    public AppointmentResponse complete(UUID id) {
        Appointment appointment = findEntity(id);
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BookingConflictException("Appointment is already completed");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        queueManager.remove(appointment.getId());

        // Fan out to billing (raise the bill) and notifications.
        eventPublisher.publishCompleted(AppointmentCompletedEvent.builder()
                .appointmentId(appointment.getId())
                .patientId(appointment.getPatientId())
                .patientName(appointment.getPatientName())
                .doctorId(appointment.getDoctorId())
                .doctorName(appointment.getDoctorName())
                .department(appointment.getDepartment())
                .consultationFee(appointment.getConsultationFee())
                .tokenNumber(appointment.getTokenNumber())
                .completedAt(Instant.now())
                .build());

        return AppointmentMapper.toResponse(appointment);
    }

    public AppointmentResponse getById(UUID id) {
        return AppointmentMapper.toResponse(findEntity(id));
    }

    public List<AppointmentResponse> list(AppointmentStatus status) {
        List<Appointment> appointments = (status != null)
                ? appointmentRepository.findByStatusOrderByCreatedAtDesc(status)
                : appointmentRepository.findAllByOrderByCreatedAtDesc();
        return appointments.stream().map(AppointmentMapper::toResponse).toList();
    }

    /** The live waiting list, ordered by the Redis priority queue. */
    public List<QueueEntryResponse> queue() {
        List<UUID> orderedIds = queueManager.orderedIds();
        if (orderedIds.isEmpty()) {
            return List.of();
        }
        Map<UUID, Appointment> byId = appointmentRepository.findAllById(orderedIds).stream()
                .collect(Collectors.toMap(Appointment::getId, a -> a));

        List<QueueEntryResponse> queue = new ArrayList<>();
        int position = 1;
        for (UUID id : orderedIds) {
            Appointment a = byId.get(id);
            // Skip anything no longer active (defensive; cancel/complete already remove it).
            if (a == null || a.getStatus() != AppointmentStatus.BOOKED) {
                continue;
            }
            queue.add(QueueEntryResponse.builder()
                    .position(position++)
                    .appointment(AppointmentMapper.toResponse(a))
                    .build());
        }
        return queue;
    }

    private TriageResultDto runTriage(BookAppointmentRequest request) {
        try {
            TriageResultDto result = triageClient.triage(
                    new TriageRequestDto(request.getSymptoms(), request.getAge(), request.getSex()));
            if (result != null && result.getUrgency() >= 1 && result.getUrgency() <= 5) {
                return result;
            }
            log.warn("Triage returned an invalid result; using default urgency");
        } catch (Exception e) {
            log.warn("Triage service unavailable, defaulting urgency: {}", e.getMessage());
        }
        return defaultTriage();
    }

    private TriageResultDto defaultTriage() {
        TriageResultDto fallback = new TriageResultDto();
        fallback.setDepartment("General Medicine");
        fallback.setUrgency(3);
        fallback.setUrgencyLabel("MODERATE");
        fallback.setRecommendedAction("Consult a doctor.");
        fallback.setReason("Automated triage was unavailable; assigned a default priority.");
        return fallback;
    }

    private DoctorDto fetchDoctor(UUID doctorId) {
        try {
            return doctorClient.getDoctor(doctorId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Doctor not found: " + doctorId);
        } catch (Exception e) {
            throw new ServiceUnavailableException("Could not reach doctor service to verify the doctor");
        }
    }

    private void updateSlot(UUID slotId, String status) {
        try {
            doctorClient.updateSlotStatus(slotId, new SlotStatusUpdateDto(status));
        } catch (Exception e) {
            log.warn("Could not set slot {} to {}: {}", slotId, status, e.getMessage());
        }
    }

    private Appointment findEntity(UUID id) {
        Optional<Appointment> found = appointmentRepository.findById(id);
        return found.orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));
    }
}
