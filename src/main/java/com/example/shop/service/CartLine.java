package com.example.shop.service;

import com.example.shop.model.Product;
import java.math.BigDecimal;

public record CartLine(Product product, int quantity) {
    public boolean isOutOfStock() { return product.getStock() == 0; }

    public boolean isExceedsStock() { return quantity > product.getStock(); }

    public BigDecimal getLineTotal() { return product.getPrice().multiply(BigDecimal.valueOf(quantity)); }
}
