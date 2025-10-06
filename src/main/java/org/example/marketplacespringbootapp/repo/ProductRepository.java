package org.example.marketplacespringbootapp.repo;

import org.example.marketplacespringbootapp.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ProductRepository {
    private final Map<Long, Product> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public List<Product> findAll() { return new ArrayList<>(store.values()); }
    public Optional<Product> findById(Long id) { return Optional.ofNullable(store.get(id)); }

    public Product save(Product p) {
        if (p.getId() == null) p.setId(seq.incrementAndGet());
        store.put(p.getId(), p);
        return p;
    }

    public boolean existsById(Long id) { return store.containsKey(id); }
    public void deleteById(Long id) { store.remove(id); }

    public void clear() { store.clear(); }
}

