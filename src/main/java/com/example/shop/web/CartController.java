package com.example.shop.web;

import com.example.shop.model.Product;
import com.example.shop.repository.ProductRepository;
import com.example.shop.service.Cart;
import com.example.shop.service.CartLine;
import com.example.shop.service.OrderService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        List<CartLine> lines = orders.resolve(cart);
        // Drop products that were deleted since they were added.
        cart.getLines().keySet().removeIf(id -> lines.stream().noneMatch(l -> l.product().getId().equals(id)));
        model.addAttribute("lines", lines);
        model.addAttribute("total", orders.total(lines));
        model.addAttribute("itemCount", cart.getItemCount());
        return "cart";
    }

    @PostMapping("/add")
    public String add(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity,
                      RedirectAttributes flash) {
        Product p = products.findById(productId).orElse(null);
        if (p == null || quantity < 1) return "redirect:/cart";
        int wanted = cart.getLines().getOrDefault(productId, 0) + quantity;
        if (p.getStock() == 0) {
            flash.addFlashAttribute("error", p.getName() + " is out of stock.");
        } else if (wanted > p.getStock()) {
            cart.set(productId, p.getStock());
            flash.addFlashAttribute("error", "Only " + p.getStock() + " of " + p.getName() + " available; cart adjusted.");
        } else {
            cart.add(productId, quantity);
            flash.addFlashAttribute("message", "Added " + p.getName() + " to your cart.");
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String update(@RequestParam Long productId, @RequestParam int quantity, RedirectAttributes flash) {
        if (!cart.getLines().containsKey(productId)) return "redirect:/cart";
        Product p = products.findById(productId).orElse(null);
        if (p != null && quantity > p.getStock()) {
            cart.set(productId, p.getStock());
            flash.addFlashAttribute("error", "Only " + p.getStock() + " of " + p.getName() + " available.");
        } else {
            cart.set(productId, quantity);
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam Long productId) {
        cart.remove(productId);
        return "redirect:/cart";
    }
}
