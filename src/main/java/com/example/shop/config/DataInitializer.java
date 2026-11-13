package com.example.shop.config;

import com.example.shop.model.Product;
import com.example.shop.model.UserAccount;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository users;
    private final ProductRepository products;
    private final PasswordEncoder encoder;
    private final String adminEmail;
    private final String adminPassword;

    public DataInitializer(UserRepository users, ProductRepository products, PasswordEncoder encoder,
                           @Value("${app.admin.email}") String adminEmail,
                           @Value("${app.admin.password}") String adminPassword) {
        this.users = users;
        this.products = products;
        this.encoder = encoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (!users.existsByEmailIgnoreCase(adminEmail)) {
            String pw = adminPassword;
            if (pw == null || pw.isBlank()) {
                byte[] b = new byte[12];
                new SecureRandom().nextBytes(b);
                pw = Base64.getUrlEncoder().withoutPadding().encodeToString(b);
                log.warn("Generated admin password for {}: {}", adminEmail, pw);
            }
            UserAccount admin = new UserAccount();
            admin.setEmail(adminEmail);
            admin.setName("Administrator");
            admin.setPasswordHash(encoder.encode(pw));
            admin.setRole("ADMIN");
            users.save(admin);
        }
        if (products.count() == 0) {
            seed("Wireless Headphones", "Over-ear Bluetooth headphones with 30h battery.", "79.99", 25, "Electronics");
            seed("Mechanical Keyboard", "Tactile switches, compact 75% layout.", "109.00", 15, "Electronics");
            seed("Running Shoes", "Lightweight road running shoes.", "64.50", 40, "Sports");
            seed("Yoga Mat", "Non-slip 6mm mat.", "24.99", 60, "Sports");
            seed("Coffee Grinder", "Burr grinder with 15 settings.", "45.00", 20, "Kitchen");
            seed("Cast Iron Skillet", "Pre-seasoned 26cm skillet.", "32.90", 30, "Kitchen");
            seed("Clean Code", "A handbook of agile software craftsmanship.", "38.00", 50, "Books");
            seed("Desk Lamp", "Dimmable LED lamp with USB port.", "29.95", 35, "Home");
        }
    }

    private void seed(String name, String desc, String price, int stock, String category) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(new BigDecimal(price));
        p.setStock(stock);
        p.setCategory(category);
        products.save(p);
    }
}
