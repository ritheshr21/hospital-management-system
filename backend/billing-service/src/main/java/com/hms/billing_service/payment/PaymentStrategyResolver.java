package com.hms.billing_service.payment;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.hms.billing_service.entity.PaymentMethod;
import com.hms.billing_service.exception.InvalidPaymentException;

/**
 * Picks the strategy for a payment method. Spring injects every
 * {@link PaymentStrategy} bean, so a new method is registered automatically.
 */
@Component
public class PaymentStrategyResolver {

    private final Map<PaymentMethod, PaymentStrategy> strategies = new EnumMap<>(PaymentMethod.class);

    public PaymentStrategyResolver(List<PaymentStrategy> allStrategies) {
        allStrategies.forEach(s -> strategies.put(s.method(), s));
    }

    public PaymentStrategy resolve(PaymentMethod method) {
        PaymentStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new InvalidPaymentException("Unsupported payment method: " + method);
        }
        return strategy;
    }
}
