package com.example.shop.web;

import com.example.shop.repository.ProductRepository;
import com.example.shop.service.Cart;
import com.example.shop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final Cart cart;
    private final OrderService orders;
    private final ProductRepository products;

    public CartController(Cart cart, OrderService orders, ProductRepository products) {
        this.cart = cart;
        this.orders = orders;
        this.products = products;
    }

    @GetMapping
    public String view(Model model) {
        var lines = orders.resolve(cart);
        model.addAttribute("lines", lines);
        model.addAttribute("total", orders.total(lines));
        return "cart";
    }

    @PostMapping("/add")
    public String add(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity) {
        if (quantity >= 1 && products.existsById(productId)) cart.add(productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String update(@RequestParam Long productId, @RequestParam int quantity) {
        if (cart.getLines().containsKey(productId)) cart.set(productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam Long productId) {
        cart.remove(productId);
        return "redirect:/cart";
    }
}
