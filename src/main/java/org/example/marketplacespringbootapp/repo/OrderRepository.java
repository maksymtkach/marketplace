package org.example.marketplacespringbootapp.repo;

import org.example.marketplacespringbootapp.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class OrderRepository {
    private final Map<Long, Order> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public Order save(Order order) {
        if (order.getId() == null) order.setId(seq.incrementAndGet());
        store.put(order.getId(), order);
        return order;
    }

    public Optional<Order> findById(Long id) { return Optional.ofNullable(store.get(id)); }
    public List<Order> findAll() { return new ArrayList<>(store.values()); }
    public void deleteById(Long id) { store.remove(id); }
}

