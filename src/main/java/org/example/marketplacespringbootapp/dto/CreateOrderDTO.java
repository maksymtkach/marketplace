package org.example.marketplacespringbootapp.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CreateOrderDTO {
    @NotEmpty
    private List<OrderItemDTO> items;

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}

