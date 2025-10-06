package org.example.marketplacespringbootapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProductDTO {
    @NotBlank
    private String sku;

    @NotBlank
    private String name;

    @NotNull @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @Min(0)
    private int stock;

    public String getSku() { return sku; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }

    public void setSku(String sku) { this.sku = sku; }
    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
}

