package org.example.marketplacespringbootapp.service;

import org.example.marketplacespringbootapp.dto.*;
import org.example.marketplacespringbootapp.exception.NotFoundException;
import org.example.marketplacespringbootapp.model.*;
import org.example.marketplacespringbootapp.repo.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repo;



    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> list() {
        return repo.findAll();
    }

    public Product get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public Product create(ProductDTO dto) {
        Product p = new Product(null, dto.getSku(), dto.getName(), dto.getPrice(), dto.getStock());
        return repo.save(p);
    }

    public Product update(Long id, ProductDTO dto) {
        Product existing = get(id);
        existing.setSku(dto.getSku());
        existing.setName(dto.getName());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());
        return repo.save(existing);
    }

    public void delete(Long id) {
        get(id); // will throw if missing
        repo.deleteById(id);
    }
}
