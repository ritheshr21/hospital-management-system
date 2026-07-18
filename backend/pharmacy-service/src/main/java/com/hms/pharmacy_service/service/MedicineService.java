package com.hms.pharmacy_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.pharmacy_service.dto.MedicineRequest;
import com.hms.pharmacy_service.dto.MedicineResponse;
import com.hms.pharmacy_service.entity.Medicine;
import com.hms.pharmacy_service.exception.PharmacyConflictException;
import com.hms.pharmacy_service.exception.ResourceNotFoundException;
import com.hms.pharmacy_service.mapper.PharmacyMapper;
import com.hms.pharmacy_service.repository.MedicineRepository;
import com.hms.pharmacy_service.stock.StockManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final StockManager stockManager;

    public MedicineResponse create(MedicineRequest request) {
        if (medicineRepository.existsByNameIgnoreCase(request.getName())) {
            throw new PharmacyConflictException("A medicine with this name already exists");
        }
        return PharmacyMapper.toResponse(medicineRepository.save(PharmacyMapper.toEntity(request)));
    }

    public MedicineResponse update(UUID id, MedicineRequest request) {
        Medicine medicine = findEntity(id);
        boolean nameChanged = !medicine.getName().equalsIgnoreCase(request.getName());
        if (nameChanged && medicineRepository.existsByNameIgnoreCase(request.getName())) {
            throw new PharmacyConflictException("A medicine with this name already exists");
        }
        PharmacyMapper.updateEntity(medicine, request);
        return PharmacyMapper.toResponse(medicineRepository.save(medicine));
    }

    public MedicineResponse getById(UUID id) {
        return PharmacyMapper.toResponse(findEntity(id));
    }

    public List<MedicineResponse> list(String search, boolean lowStockOnly) {
        List<Medicine> medicines;
        if (lowStockOnly) {
            medicines = stockManager.lowStock();
        } else if (search != null && !search.isBlank()) {
            medicines = medicineRepository.search(search.trim());
        } else {
            medicines = medicineRepository.findAll();
        }
        return medicines.stream().map(PharmacyMapper::toResponse).toList();
    }

    @Transactional
    public MedicineResponse restock(UUID id, int quantity) {
        Medicine medicine = findEntity(id);
        return PharmacyMapper.toResponse(stockManager.restock(medicine, quantity));
    }

    public void delete(UUID id) {
        medicineRepository.delete(findEntity(id));
    }

    private Medicine findEntity(UUID id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found: " + id));
    }
}
