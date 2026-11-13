package com.example.shop.service;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/** Per-session shopping cart: product id to quantity. */
@Component
@SessionScope
public class Cart implements Serializable {
    public static final int MAX_PER_ITEM = 99;
    private final Map<Long, Integer> lines = new LinkedHashMap<>();

    public Map<Long, Integer> getLines() { return lines; }

    public void add(Long productId, int qty) {
        lines.merge(productId, qty, (a, b) -> Math.min(MAX_PER_ITEM, a + b));
    }

    public void set(Long productId, int qty) {
        if (qty <= 0) lines.remove(productId);
        else lines.put(productId, Math.min(MAX_PER_ITEM, qty));
    }

    public int getItemCount() { return lines.values().stream().mapToInt(Integer::intValue).sum(); }

    public void remove(Long productId) { lines.remove(productId); }
    public void clear() { lines.clear(); }
    public boolean isEmpty() { return lines.isEmpty(); }
}
