package com.example.shop.service;

public record PaymentResult(boolean approved, String reference, String message) {}
