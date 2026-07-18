package com.hms.billing_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.billing_service.dto.BillResponse;
import com.hms.billing_service.dto.CreateBillRequest;
import com.hms.billing_service.dto.PaymentRequest;
import com.hms.billing_service.entity.Bill;
import com.hms.billing_service.entity.BillItem;
import com.hms.billing_service.entity.BillStatus;
import com.hms.billing_service.event.AppointmentCompletedEvent;
import com.hms.billing_service.event.BillEventPublisher;
import com.hms.billing_service.event.BillPaidEvent;
import com.hms.billing_service.exception.BillConflictException;
import com.hms.billing_service.exception.ResourceNotFoundException;
import com.hms.billing_service.mapper.BillMapper;
import com.hms.billing_service.payment.PaymentResult;
import com.hms.billing_service.payment.PaymentStrategy;
import com.hms.billing_service.payment.PaymentStrategyResolver;
import com.hms.billing_service.repository.BillRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillService {

    private static final Logger log = LoggerFactory.getLogger(BillService.class);

    private final BillRepository billRepository;
    private final PaymentStrategyResolver strategyResolver;
    private final BillEventPublisher eventPublisher;

    @Value("${hms.billing.tax-percent}")
    private BigDecimal taxPercent;

    @Value("${hms.billing.default-consultation-fee}")
    private BigDecimal defaultConsultationFee;

    public BillResponse create(CreateBillRequest request) {
        if (request.getAppointmentId() != null
                && billRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new BillConflictException("A bill already exists for this appointment");
        }

        Bill bill = Bill.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .patientName(request.getPatientName())
                .doctorName(request.getDoctorName())
                .department(request.getDepartment())
                .status(BillStatus.PENDING)
                .build();
        bill.getItems().addAll(BillMapper.toItemEntities(request.getItems()));

        applyTotals(bill);
        bill.setInvoiceNumber(nextInvoiceNumber());
        return BillMapper.toResponse(billRepository.save(bill));
    }

    /**
     * Kafka-driven bill creation. Idempotent: Kafka can redeliver, and we must
     * not bill a patient twice for the same appointment.
     */
    @Transactional
    public void createFromAppointment(AppointmentCompletedEvent event) {
        if (event.getAppointmentId() == null) {
            log.warn("Ignoring appointment_completed with no appointmentId");
            return;
        }
        if (billRepository.existsByAppointmentId(event.getAppointmentId())) {
            log.info("Bill already exists for appointment {}, skipping", event.getAppointmentId());
            return;
        }

        BigDecimal fee = event.getConsultationFee() != null
                ? event.getConsultationFee()
                : defaultConsultationFee;

        BillItem consultation = BillItem.builder()
                .description("Consultation - " + (event.getDepartment() != null ? event.getDepartment() : "General"))
                .quantity(1)
                .unitPrice(fee)
                .lineTotal(fee)
                .build();

        Bill bill = Bill.builder()
                .appointmentId(event.getAppointmentId())
                .patientId(event.getPatientId())
                .patientName(event.getPatientName())
                .doctorName(event.getDoctorName())
                .department(event.getDepartment())
                .status(BillStatus.PENDING)
                .build();
        bill.getItems().add(consultation);

        applyTotals(bill);
        bill.setInvoiceNumber(nextInvoiceNumber());
        Bill saved = billRepository.save(bill);
        log.info("Raised invoice {} for appointment {}", saved.getInvoiceNumber(), event.getAppointmentId());
    }

    public BillResponse pay(UUID id, PaymentRequest request) {
        Bill bill = findEntity(id);
        if (bill.getStatus() == BillStatus.PAID) {
            throw new BillConflictException("This bill is already paid");
        }
        if (bill.getStatus() == BillStatus.CANCELLED) {
            throw new BillConflictException("Cannot pay a cancelled bill");
        }

        // Strategy pattern: the method decides how the total is settled.
        PaymentStrategy strategy = strategyResolver.resolve(request.getMethod());
        PaymentResult result = strategy.pay(bill.getTotal(), request);

        bill.setStatus(BillStatus.PAID);
        bill.setPaymentMethod(request.getMethod());
        bill.setPaymentReference(result.reference());
        bill.setPaymentNote(result.note());
        bill.setInsuranceCovered(result.insuranceCovered());
        bill.setAmountPaid(result.collected());
        bill.setPaidAt(Instant.now());

        Bill saved = billRepository.save(bill);

        eventPublisher.publishPaid(BillPaidEvent.builder()
                .billId(saved.getId())
                .invoiceNumber(saved.getInvoiceNumber())
                .appointmentId(saved.getAppointmentId())
                .patientId(saved.getPatientId())
                .patientName(saved.getPatientName())
                .total(saved.getTotal())
                .insuranceCovered(saved.getInsuranceCovered())
                .amountPaid(saved.getAmountPaid())
                .paymentMethod(saved.getPaymentMethod().name())
                .paymentReference(saved.getPaymentReference())
                .paidAt(saved.getPaidAt())
                .build());

        return BillMapper.toResponse(saved);
    }

    public BillResponse cancel(UUID id) {
        Bill bill = findEntity(id);
        if (bill.getStatus() == BillStatus.PAID) {
            throw new BillConflictException("Cannot cancel a paid bill");
        }
        bill.setStatus(BillStatus.CANCELLED);
        return BillMapper.toResponse(billRepository.save(bill));
    }

    public BillResponse getById(UUID id) {
        return BillMapper.toResponse(findEntity(id));
    }

    public BillResponse getByAppointment(UUID appointmentId) {
        return billRepository.findByAppointmentId(appointmentId)
                .map(BillMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No bill for appointment: " + appointmentId));
    }

    public List<BillResponse> list(BillStatus status) {
        List<Bill> bills = (status != null)
                ? billRepository.findByStatusOrderByCreatedAtDesc(status)
                : billRepository.findAllByOrderByCreatedAtDesc();
        return bills.stream().map(BillMapper::toResponse).toList();
    }

    /** subtotal from line items, then tax on top. */
    private void applyTotals(Bill bill) {
        BigDecimal subtotal = bill.getItems().stream()
                .map(BillItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = subtotal.multiply(taxPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        bill.setSubtotal(subtotal);
        bill.setTax(tax);
        bill.setTotal(subtotal.add(tax).setScale(2, RoundingMode.HALF_UP));
    }

    private String nextInvoiceNumber() {
        long sequence = billRepository.countByInvoiceNumberIsNotNull() + 1;
        return "INV-%d-%06d".formatted(LocalDate.now().getYear(), sequence);
    }

    private Bill findEntity(UUID id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found: " + id));
    }
}
