package com.example.shop.service;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Simulated card gateway for development and demos. It never contacts a real processor.
 * Test cards: 4000 0000 0000 0002 is declined, 4000 0000 0000 9995 has insufficient funds,
 * and every other valid card number is approved.
 */
@Component
public class MockPaymentGateway implements PaymentGateway {
    @Override
    public PaymentResult charge(CardDetails card, BigDecimal amount) {
        return switch (card.number()) {
            case "4000000000000002" -> new PaymentResult(false, null, "Your card was declined.");
            case "4000000000009995" -> new PaymentResult(false, null, "Your card has insufficient funds.");
            default -> new PaymentResult(true, "MOCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), "Approved");
        };
    }
}
