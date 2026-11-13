package com.example.shop.service;

import com.example.shop.model.Product;
import java.math.BigDecimal;

public record CartLine(Product product, int quantity) {
    public BigDecimal getLineTotal() { return product.getPrice().multiply(BigDecimal.valueOf(quantity)); }
}
