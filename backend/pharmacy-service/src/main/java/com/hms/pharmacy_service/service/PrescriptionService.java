package com.hms.pharmacy_service.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.pharmacy_service.dto.PrescriptionRequest;
import com.hms.pharmacy_service.dto.PrescriptionResponse;
import com.hms.pharmacy_service.entity.Medicine;
import com.hms.pharmacy_service.entity.Prescription;
import com.hms.pharmacy_service.entity.PrescriptionItem;
import com.hms.pharmacy_service.entity.PrescriptionStatus;
import com.hms.pharmacy_service.exception.PharmacyConflictException;
import com.hms.pharmacy_service.exception.ResourceNotFoundException;
import com.hms.pharmacy_service.mapper.PharmacyMapper;
import com.hms.pharmacy_service.repository.MedicineRepository;
import com.hms.pharmacy_service.repository.PrescriptionRepository;
import com.hms.pharmacy_service.stock.StockManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;
    private final StockManager stockManager;

    public PrescriptionResponse create(PrescriptionRequest request) {
        Prescription prescription = Prescription.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .patientName(request.getPatientName())
                .doctorId(request.getDoctorId())
                .doctorName(request.getDoctorName())
                .notes(request.getNotes())
                .status(PrescriptionStatus.ISSUED)
                .build();

        List<PrescriptionItem> items = PharmacyMapper.toItemEntities(request.getItems());
        // Validate every medicine exists and snapshot its name onto the item.
        for (PrescriptionItem item : items) {
            Medicine medicine = medicineRepository.findById(item.getMedicineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + item.getMedicineId()));
            item.setMedicineName(medicine.getName());
        }
        prescription.getItems().addAll(items);

        return PharmacyMapper.toResponse(prescriptionRepository.save(prescription));
    }

    /**
     * Dispensing is the only path that consumes stock. It is transactional so a
     * failure part-way (e.g. the third medicine is short) rolls back the earlier
     * deductions — a prescription is all-or-nothing.
     */
    @Transactional
    public PrescriptionResponse dispense(UUID id) {
        Prescription prescription = findEntity(id);
        if (prescription.getStatus() == PrescriptionStatus.DISPENSED) {
            throw new PharmacyConflictException("This prescription is already dispensed");
        }
        if (prescription.getStatus() == PrescriptionStatus.CANCELLED) {
            throw new PharmacyConflictException("Cannot dispense a cancelled prescription");
        }

        stockManager.assertAvailable(prescription.getItems());
        stockManager.deduct(prescription.getItems());

        prescription.setStatus(PrescriptionStatus.DISPENSED);
        prescription.setDispensedAt(Instant.now());
        return PharmacyMapper.toResponse(prescriptionRepository.save(prescription));
    }

    public PrescriptionResponse cancel(UUID id) {
        Prescription prescription = findEntity(id);
        if (prescription.getStatus() == PrescriptionStatus.DISPENSED) {
            throw new PharmacyConflictException("Cannot cancel a dispensed prescription");
        }
        prescription.setStatus(PrescriptionStatus.CANCELLED);
        return PharmacyMapper.toResponse(prescriptionRepository.save(prescription));
    }

    public PrescriptionResponse getById(UUID id) {
        return PharmacyMapper.toResponse(findEntity(id));
    }

    public List<PrescriptionResponse> list(PrescriptionStatus status) {
        List<Prescription> prescriptions = (status != null)
                ? prescriptionRepository.findByStatusOrderByCreatedAtDesc(status)
                : prescriptionRepository.findAllByOrderByCreatedAtDesc();
        return prescriptions.stream().map(PharmacyMapper::toResponse).toList();
    }

    private Prescription findEntity(UUID id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found: " + id));
    }
}
