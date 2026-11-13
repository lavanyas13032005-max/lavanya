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
    private final PaymentGateway gateway;

    public OrderService(OrderRepository orders, ProductRepository products, UserRepository users,
                        PaymentGateway gateway) {
        this.gateway = gateway;
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

    /**
     * Checks stock, charges the card, then creates a PAID order and decrements stock, all or nothing.
     * A declined card throws before anything is changed, so the cart and stock are untouched.
     */
    @Transactional
    public CustomerOrder checkout(String email, Cart cart, String address, CardDetails card) {
        if (cart.isEmpty()) throw new IllegalStateException("Cart is empty");
        UserAccount user = users.findByEmailIgnoreCase(email).orElseThrow();
        CustomerOrder order = new CustomerOrder();
        order.setUser(user);
        order.setShippingAddress(address);
        BigDecimal total = BigDecimal.ZERO;
        List<Product> locked = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : cart.getLines().entrySet()) {
            Product p = products.findForUpdateById(e.getKey())
                    .orElseThrow(() -> new OutOfStockException("A product in your cart is no longer available"));
            int qty = e.getValue();
            if (p.getStock() < qty) {
                throw new OutOfStockException("Only " + p.getStock() + " left of " + p.getName());
            }
            OrderItem item = new OrderItem();
            item.setProduct(p);
            item.setProductName(p.getName());
            item.setQuantity(qty);
            item.setUnitPrice(p.getPrice());
            order.getItems().add(item);
            locked.add(p);
            total = total.add(item.getLineTotal());
        }
        order.setTotal(total);

        PaymentResult payment = gateway.charge(card, total);
        if (!payment.approved()) throw new PaymentDeclinedException(payment.message());

        for (OrderItem i : order.getItems()) i.getProduct().setStock(i.getProduct().getStock() - i.getQuantity());
        order.markPaid(payment.reference(), card.brand(), card.last4());
        CustomerOrder saved = orders.save(order);
        cart.clear();
        return saved;
    }

    @Transactional(readOnly = true)
    public List<CustomerOrder> forUser(String email) {
        return orders.findByUserEmailIgnoreCaseOrderByCreatedAtDesc(email);
    }

    @Transactional(readOnly = true)
    public long countByStatus(OrderStatus status) { return orders.countByStatus(status); }

    @Transactional(readOnly = true)
    public List<CustomerOrder> all() { return orders.findAllByOrderByCreatedAtDesc(); }

    @Transactional(readOnly = true)
    public CustomerOrder get(Long id) { return orders.findWithItemsById(id).orElseThrow(); }

    /** Cancelling restores stock. Cancelling a PAID order does not refund the card in this mock setup. */
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
