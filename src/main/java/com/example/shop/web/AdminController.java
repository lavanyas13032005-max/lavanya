package com.example.shop.web;

import com.example.shop.model.OrderStatus;
import com.example.shop.model.Product;
import com.example.shop.repository.ProductRepository;
import com.example.shop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
    static final int LOW_STOCK = 5;
    static final int MAX_STOCK = 1_000_000;

    private final ProductRepository products;
    private final OrderService orders;

    public AdminController(ProductRepository products, OrderService orders) {
        this.products = products;
        this.orders = orders;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("productCount", products.count());
        model.addAttribute("outOfStock", products.countByStock(0));
        model.addAttribute("lowStock", products.countByStockBetween(1, LOW_STOCK));
        model.addAttribute("inventoryValue", products.inventoryValue());
        model.addAttribute("placedOrders", orders.countByStatus(OrderStatus.PAID));
        model.addAttribute("attention", products.adminSearch(null, LOW_STOCK));
        model.addAttribute("lowStockLimit", LOW_STOCK);
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String list(@RequestParam(defaultValue = "") String q,
                       @RequestParam(defaultValue = "false") boolean lowOnly, Model model) {
        model.addAttribute("products", products.adminSearch(q.isBlank() ? null : q.trim(), lowOnly ? LOW_STOCK : null));
        model.addAttribute("q", q);
        model.addAttribute("lowOnly", lowOnly);
        model.addAttribute("lowStockLimit", LOW_STOCK);
        return "admin/products";
    }

    @PostMapping("/products/{id}/stock")
    public String updateStock(@PathVariable Long id, @RequestParam int stock, RedirectAttributes flash) {
        Product p = products.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (stock < 0 || stock > MAX_STOCK) {
            flash.addFlashAttribute("error", "Stock must be between 0 and " + MAX_STOCK + ".");
        } else {
            p.setStock(stock);
            products.save(p);
            flash.addFlashAttribute("message", "Stock for " + p.getName() + " set to " + stock + ".");
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products/new")
    public String newForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/product-form";
    }

    @GetMapping("/products/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("product", products.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        return "admin/product-form";
    }

    @PostMapping("/products")
    public String save(@Valid @ModelAttribute("product") Product form, BindingResult result,
                       RedirectAttributes flash) {
        if (result.hasErrors()) return "admin/product-form";
        if (form.getStock() > MAX_STOCK) {
            result.rejectValue("stock", "max", "Stock must be at most " + MAX_STOCK);
            return "admin/product-form";
        }
        Product target = form;
        if (form.getId() != null) {
            target = products.findById(form.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            target.setName(form.getName());
            target.setDescription(form.getDescription());
            target.setPrice(form.getPrice());
            target.setStock(form.getStock());
            target.setCategory(form.getCategory());
            target.setImageUrl(form.getImageUrl());
        }
        products.save(target);
        flash.addFlashAttribute("message", "Saved " + target.getName() + ".");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        try {
            products.deleteById(id);
            products.flush();
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("error", "Product appears in existing orders; set its stock to 0 instead.");
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String orderList(Model model) {
        model.addAttribute("orders", orders.all());
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String status(@PathVariable Long id, @RequestParam OrderStatus status, RedirectAttributes flash) {
        try {
            orders.updateStatus(id, status);
        } catch (IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }
}
