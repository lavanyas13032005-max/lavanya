package com.example.shop.service;

public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String message) { super(message); }
}
