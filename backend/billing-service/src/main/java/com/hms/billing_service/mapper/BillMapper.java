package com.hms.billing_service.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.hms.billing_service.dto.BillItemDto;
import com.hms.billing_service.dto.BillResponse;
import com.hms.billing_service.entity.Bill;
import com.hms.billing_service.entity.BillItem;

public final class BillMapper {

    private BillMapper() {
    }

    public static BillItem toItemEntity(BillItemDto dto) {
        BigDecimal unit = dto.getUnitPrice();
        int qty = Math.max(1, dto.getQuantity());
        return BillItem.builder()
                .description(dto.getDescription())
                .quantity(qty)
                .unitPrice(unit)
                .lineTotal(unit.multiply(BigDecimal.valueOf(qty)))
                .build();
    }

    public static List<BillItem> toItemEntities(List<BillItemDto> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        return dtos.stream().map(BillMapper::toItemEntity)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static BillItemDto toItemDto(BillItem item) {
        BillItemDto dto = new BillItemDto();
        dto.setId(item.getId());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setLineTotal(item.getLineTotal());
        return dto;
    }

    public static BillResponse toResponse(Bill b) {
        return BillResponse.builder()
                .id(b.getId())
                .invoiceNumber(b.getInvoiceNumber())
                .appointmentId(b.getAppointmentId())
                .patientId(b.getPatientId())
                .patientName(b.getPatientName())
                .doctorName(b.getDoctorName())
                .department(b.getDepartment())
                .items(b.getItems().stream().map(BillMapper::toItemDto).toList())
                .subtotal(b.getSubtotal())
                .tax(b.getTax())
                .total(b.getTotal())
                .status(b.getStatus())
                .paymentMethod(b.getPaymentMethod())
                .paymentReference(b.getPaymentReference())
                .insuranceCovered(b.getInsuranceCovered())
                .amountPaid(b.getAmountPaid())
                .paymentNote(b.getPaymentNote())
                .paidAt(b.getPaidAt())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
