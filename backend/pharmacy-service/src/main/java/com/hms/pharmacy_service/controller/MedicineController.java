package com.hms.pharmacy_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hms.pharmacy_service.dto.MedicineRequest;
import com.hms.pharmacy_service.dto.MedicineResponse;
import com.hms.pharmacy_service.dto.RestockRequest;
import com.hms.pharmacy_service.service.MedicineService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicineResponse create(@Valid @RequestBody MedicineRequest request) {
        return medicineService.create(request);
    }

    @GetMapping
    public List<MedicineResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") boolean lowStock) {
        return medicineService.list(search, lowStock);
    }

    @GetMapping("/{id}")
    public MedicineResponse getById(@PathVariable UUID id) {
        return medicineService.getById(id);
    }

    @PutMapping("/{id}")
    public MedicineResponse update(@PathVariable UUID id, @Valid @RequestBody MedicineRequest request) {
        return medicineService.update(id, request);
    }

    /** Stock only ever moves through here or through dispensing. */
    @PostMapping("/{id}/restock")
    public MedicineResponse restock(@PathVariable UUID id, @Valid @RequestBody RestockRequest request) {
        return medicineService.restock(id, request.getQuantity());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        medicineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
