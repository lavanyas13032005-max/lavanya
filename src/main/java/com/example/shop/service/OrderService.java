package com.example.shop.service;

import com.example.shop.model.*;
import com.example.shop.repository.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final ProductRepository products;
    private final UserRepository users;

    public OrderService(OrderRepository orders, ProductRepository products, UserRepository users) {
        this.orders = orders;
        this.products = products;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<CartLine> resolve(Cart cart) {
        List<CartLine> result = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : cart.getLines().entrySet()) {
            products.findById(e.getKey()).ifPresent(p -> result.add(new CartLine(p, e.getValue())));
        }
        return result;
    }

    public BigDecimal total(List<CartLine> lines) {
        return lines.stream().map(CartLine::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Creates an order from the cart and decrements stock, all or nothing. */
    @Transactional
    public CustomerOrder checkout(String email, Cart cart, String address) {
        if (cart.isEmpty()) throw new IllegalStateException("Cart is empty");
        UserAccount user = users.findByEmailIgnoreCase(email).orElseThrow();
        CustomerOrder order = new CustomerOrder();
        order.setUser(user);
        order.setShippingAddress(address);
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> e : cart.getLines().entrySet()) {
            Product p = products.findById(e.getKey())
                    .orElseThrow(() -> new OutOfStockException("A product in your cart is no longer available"));
            int qty = e.getValue();
            if (p.getStock() < qty) {
                throw new OutOfStockException("Only " + p.getStock() + " left of " + p.getName());
            }
            p.setStock(p.getStock() - qty);
            OrderItem item = new OrderItem();
            item.setProduct(p);
            item.setProductName(p.getName());
            item.setQuantity(qty);
            item.setUnitPrice(p.getPrice());
            order.getItems().add(item);
            total = total.add(item.getLineTotal());
        }
        order.setTotal(total);
        CustomerOrder saved = orders.save(order);
        cart.clear();
        return saved;
    }

    @Transactional(readOnly = true)
    public List<CustomerOrder> forUser(String email) {
        return orders.findByUserEmailIgnoreCaseOrderByCreatedAtDesc(email);
    }

    @Transactional(readOnly = true)
    public List<CustomerOrder> all() { return orders.findAllByOrderByCreatedAtDesc(); }

    @Transactional(readOnly = true)
    public CustomerOrder get(Long id) { return orders.findWithItemsById(id).orElseThrow(); }

    /** Cancelling restores stock. Only PLACED orders can be cancelled. */
    @Transactional
    public void updateStatus(Long id, OrderStatus status) {
        CustomerOrder o = orders.findWithItemsById(id).orElseThrow();
        if (o.getStatus() == status) return;
        if (o.getStatus() == OrderStatus.CANCELLED || o.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Order is already " + o.getStatus());
        }
        if (status == OrderStatus.CANCELLED) {
            for (OrderItem i : o.getItems()) {
                Product p = i.getProduct();
                p.setStock(p.getStock() + i.getQuantity());
            }
        }
        o.setStatus(status);
    }
}
