package com.hms.billing_service.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hms.billing_service.dto.PaymentRequest;
import com.hms.billing_service.entity.PaymentMethod;
import com.hms.billing_service.exception.InvalidPaymentException;

/**
 * Unlike the direct methods, an insurer covers a configured share of the bill
 * and only the remaining co-pay is collected from the patient.
 */
@Component
public class InsurancePaymentStrategy implements PaymentStrategy {

    @Value("${hms.billing.insurance-coverage-percent}")
    private BigDecimal coveragePercent;

    @Override
    public PaymentMethod method() {
        return PaymentMethod.INSURANCE;
    }

    @Override
    public PaymentResult pay(BigDecimal total, PaymentRequest request) {
        String policy = request.getPolicyNumber();
        if (policy == null || policy.isBlank()) {
            throw new InvalidPaymentException("Insurance payment requires a policy number");
        }

        BigDecimal covered = total.multiply(coveragePercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal coPay = total.subtract(covered).setScale(2, RoundingMode.HALF_UP);

        String note = coveragePercent + "% covered by insurer (policy " + policy + "); co-pay " + coPay + " collected";
        return new PaymentResult("INS-" + policy, note, covered, coPay);
    }
}
