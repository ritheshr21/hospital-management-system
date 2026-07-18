package com.hms.appointment_service.mapper;

import com.hms.appointment_service.dto.AppointmentResponse;
import com.hms.appointment_service.entity.Appointment;

public final class AppointmentMapper {

    private AppointmentMapper() {
    }

    public static AppointmentResponse toResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .patientId(a.getPatientId())
                .patientName(a.getPatientName())
                .doctorId(a.getDoctorId())
                .doctorName(a.getDoctorName())
                .department(a.getDepartment())
                .consultationFee(a.getConsultationFee())
                .slotId(a.getSlotId())
                .date(a.getDate())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .symptoms(a.getSymptoms())
                .urgency(a.getUrgency())
                .urgencyLabel(a.getUrgencyLabel())
                .triageDepartment(a.getTriageDepartment())
                .triageReason(a.getTriageReason())
                .recommendedAction(a.getRecommendedAction())
                .tokenNumber(a.getTokenNumber())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
