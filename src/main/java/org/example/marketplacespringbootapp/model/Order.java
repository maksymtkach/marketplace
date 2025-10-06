package org.example.marketplacespringbootapp.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {
    private Long id;
    private String customerUsername; // owner
    private List<OrderItem> items = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;
    private Instant createdAt = Instant.now();

    public Order() {}

    public Order(Long id, String customerUsername) {
        this.id = id;
        this.customerUsername = customerUsername;
    }

    public Long getId() { return id; }
    public String getCustomerUsername() { return customerUsername; }
    public List<OrderItem> getItems() { return items; }
    public BigDecimal getTotal() { return total; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setCustomerUsername(String customerUsername) { this.customerUsername = customerUsername; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order that)) return false;
        return Objects.equals(id, that.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}

