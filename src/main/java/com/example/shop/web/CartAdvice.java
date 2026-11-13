package com.example.shop.web;

import com.example.shop.service.Cart;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Exposes the cart's item count to every page for the navigation bar. */
@ControllerAdvice
public class CartAdvice {
    private final Cart cart;

    public CartAdvice(Cart cart) { this.cart = cart; }

    @ModelAttribute("cartCount")
    public int cartCount() { return cart.getItemCount(); }
}
