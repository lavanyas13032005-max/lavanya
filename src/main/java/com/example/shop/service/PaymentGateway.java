package com.example.shop.service;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentResult charge(CardDetails card, BigDecimal amount);
}
