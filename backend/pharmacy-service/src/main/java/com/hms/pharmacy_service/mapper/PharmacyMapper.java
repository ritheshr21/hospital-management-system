package com.hms.pharmacy_service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.hms.pharmacy_service.dto.MedicineRequest;
import com.hms.pharmacy_service.dto.MedicineResponse;
import com.hms.pharmacy_service.dto.PrescriptionItemDto;
import com.hms.pharmacy_service.dto.PrescriptionResponse;
import com.hms.pharmacy_service.entity.Medicine;
import com.hms.pharmacy_service.entity.Prescription;
import com.hms.pharmacy_service.entity.PrescriptionItem;

public final class PharmacyMapper {

    private PharmacyMapper() {
    }

    public static Medicine toEntity(MedicineRequest req) {
        return Medicine.builder()
                .name(req.getName())
                .brand(req.getBrand())
                .category(req.getCategory())
                .unit(req.getUnit())
                .price(req.getPrice())
                .stockQuantity(req.getStockQuantity())
                .reorderLevel(req.getReorderLevel())
                .expiryDate(req.getExpiryDate())
                .active(req.getActive() == null || req.getActive())
                .build();
    }

    /** Note: stock is deliberately NOT editable here — it moves only via StockManager. */
    public static void updateEntity(Medicine medicine, MedicineRequest req) {
        medicine.setName(req.getName());
        medicine.setBrand(req.getBrand());
        medicine.setCategory(req.getCategory());
        medicine.setUnit(req.getUnit());
        medicine.setPrice(req.getPrice());
        medicine.setReorderLevel(req.getReorderLevel());
        medicine.setExpiryDate(req.getExpiryDate());
        if (req.getActive() != null) {
            medicine.setActive(req.getActive());
        }
    }

    public static MedicineResponse toResponse(Medicine m) {
        return MedicineResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .brand(m.getBrand())
                .category(m.getCategory())
                .unit(m.getUnit())
                .price(m.getPrice())
                .stockQuantity(m.getStockQuantity())
                .reorderLevel(m.getReorderLevel())
                .expiryDate(m.getExpiryDate())
                .active(m.isActive())
                .lowStock(m.isLowStock())
                .build();
    }

    public static List<PrescriptionItem> toItemEntities(List<PrescriptionItemDto> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        return dtos.stream()
                .map(d -> PrescriptionItem.builder()
                        .medicineId(d.getMedicineId())
                        .medicineName(d.getMedicineName())
                        .dosage(d.getDosage())
                        .durationDays(d.getDurationDays())
                        .quantity(d.getQuantity())
                        .instructions(d.getInstructions())
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static PrescriptionItemDto toItemDto(PrescriptionItem i) {
        PrescriptionItemDto dto = new PrescriptionItemDto();
        dto.setId(i.getId());
        dto.setMedicineId(i.getMedicineId());
        dto.setMedicineName(i.getMedicineName());
        dto.setDosage(i.getDosage());
        dto.setDurationDays(i.getDurationDays());
        dto.setQuantity(i.getQuantity());
        dto.setInstructions(i.getInstructions());
        return dto;
    }

    public static PrescriptionResponse toResponse(Prescription p) {
        return PrescriptionResponse.builder()
                .id(p.getId())
                .appointmentId(p.getAppointmentId())
                .patientId(p.getPatientId())
                .patientName(p.getPatientName())
                .doctorId(p.getDoctorId())
                .doctorName(p.getDoctorName())
                .notes(p.getNotes())
                .items(p.getItems().stream().map(PharmacyMapper::toItemDto).toList())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .dispensedAt(p.getDispensedAt())
                .build();
    }
}
