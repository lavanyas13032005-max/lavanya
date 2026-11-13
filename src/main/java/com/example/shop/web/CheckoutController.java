package com.example.shop.web;

import com.example.shop.model.CustomerOrder;
import com.example.shop.service.Cart;
import com.example.shop.service.OrderService;
import com.example.shop.service.OutOfStockException;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class CheckoutController {
    private final Cart cart;
    private final OrderService orders;

    public CheckoutController(Cart cart, OrderService orders) {
        this.cart = cart;
        this.orders = orders;
    }

    @GetMapping("/checkout")
    public String form(Model model) {
        if (cart.isEmpty()) return "redirect:/cart";
        var lines = orders.resolve(cart);
        model.addAttribute("lines", lines);
        model.addAttribute("total", orders.total(lines));
        return "checkout";
    }

    @PostMapping("/checkout")
    public String place(@RequestParam String address, Principal principal, Model model) {
        String addr = address.trim();
        if (cart.isEmpty()) return "redirect:/cart";
        var lines = orders.resolve(cart);
        if (addr.isEmpty() || addr.length() > 300) {
            return fail(model, lines, "Please enter a shipping address (max 300 characters)");
        }
        try {
            CustomerOrder o = orders.checkout(principal.getName(), cart, addr);
            return "redirect:/orders/" + o.getId() + "?placed";
        } catch (OutOfStockException e) {
            return fail(model, lines, e.getMessage());
        }
    }

    private String fail(Model model, java.util.List<com.example.shop.service.CartLine> lines, String error) {
        model.addAttribute("lines", lines);
        model.addAttribute("total", orders.total(lines));
        model.addAttribute("error", error);
        return "checkout";
    }

    @GetMapping("/orders")
    public String list(Principal principal, Model model) {
        model.addAttribute("orders", orders.forUser(principal.getName()));
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String detail(@PathVariable Long id, Principal principal, Model model) {
        CustomerOrder o = orders.get(id);
        // Customers may only see their own orders.
        if (!o.getUser().getEmail().equalsIgnoreCase(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        model.addAttribute("order", o);
        return "order";
    }
}
