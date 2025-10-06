package org.example.marketplacespringbootapp.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {
    private Long id;
    private String sku;
    private String name;
    private BigDecimal price;
    private int stock;

    public Product() { }

    public Product(Long id, String sku, String name, BigDecimal price, int stock) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }

    public void setId(Long id) { this.id = id; }
    public void setSku(String sku) { this.sku = sku; }
    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product p)) return false;
        return Objects.equals(id, p.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}

