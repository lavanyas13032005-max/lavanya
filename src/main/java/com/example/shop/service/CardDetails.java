package com.example.shop.service;

/** Card data passed to the gateway for one charge. Never persisted or logged. */
public record CardDetails(String number, String holderName, int expiryMonth, int expiryYear, String cvc) {
    public String last4() { return number.substring(number.length() - 4); }

    public String brand() {
        if (number.startsWith("4")) return "Visa";
        if (number.matches("^(5[1-5]|2[2-7]).*")) return "Mastercard";
        if (number.matches("^3[47].*")) return "Amex";
        return "Card";
    }

    @Override
    public String toString() { return "CardDetails[" + brand() + " ****" + last4() + "]"; }
}
