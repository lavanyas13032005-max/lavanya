package com.example.shop.web;

import com.example.shop.model.CustomerOrder;
import com.example.shop.service.Cart;
import com.example.shop.service.OrderService;
import com.example.shop.service.OutOfStockException;
import com.example.shop.service.CardForm;
import com.example.shop.service.CartLine;
import com.example.shop.service.PaymentDeclinedException;
import com.example.shop.service.ShippingForm;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String form(@ModelAttribute("form") ShippingForm form, @ModelAttribute("card") CardForm card, Model model) {
        if (cart.isEmpty()) return "redirect:/cart";
        addSummary(model, orders.resolve(cart));
        return "checkout";
    }

    @PostMapping("/checkout")
    public String place(@Valid @ModelAttribute("form") ShippingForm form, BindingResult result,
                        @Valid @ModelAttribute("card") CardForm card, BindingResult cardResult,
                        Principal principal, Model model) {
        if (cart.isEmpty()) return "redirect:/cart";
        var lines = orders.resolve(cart);
        if (!cardResult.hasFieldErrors("number") && !card.hasValidChecksum()) {
            cardResult.rejectValue("number", "checksum", "Enter a valid card number");
        }
        if (!cardResult.hasFieldErrors("expiry") && card.isExpired()) {
            cardResult.rejectValue("expiry", "expired", "This card has expired");
        }
        if (result.hasErrors() || cardResult.hasErrors()) return redisplay(model, lines);
        try {
            CustomerOrder o = orders.checkout(principal.getName(), cart, form.toAddressText(), card.toDetails());
            return "redirect:/orders/" + o.getId() + "?placed";
        } catch (OutOfStockException | PaymentDeclinedException e) {
            model.addAttribute("error", e.getMessage());
            return redisplay(model, lines);
        }
    }

    private String redisplay(Model model, List<CartLine> lines) {
        addSummary(model, lines);
        return "checkout";
    }

    private void addSummary(Model model, List<CartLine> lines) {
        model.addAttribute("lines", lines);
        model.addAttribute("total", orders.total(lines));
        model.addAttribute("itemCount", cart.getItemCount());
        model.addAttribute("hasStockProblem", lines.stream().anyMatch(CartLine::isExceedsStock));
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
