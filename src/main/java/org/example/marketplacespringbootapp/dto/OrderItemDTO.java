package org.example.marketplacespringbootapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderItemDTO {
    @NotNull
    private Long productId;

    @Min(1)
    private int quantity;

    public Long getProductId() { return productId; }
    public int getQuantity() { return quantity; }

    public void setProductId(Long productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

