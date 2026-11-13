package com.example.shop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 120)
    private String name;

    @Size(max = 2000)
    @Column(length = 2000)
    private String description;

    @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2)
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Min(0)
    private int stock;

    @Size(max = 60)
    private String category;

    @Size(max = 500) @Pattern(regexp = "^(https?://\\S+)?$", message = "must be an http(s) URL")
    @Column(length = 500)
    private String imageUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = (imageUrl == null || imageUrl.isBlank()) ? null : imageUrl.trim(); }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
