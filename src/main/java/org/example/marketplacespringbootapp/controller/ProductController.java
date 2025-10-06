package org.example.marketplacespringbootapp.controller;

import jakarta.validation.Valid;
import org.example.marketplacespringbootapp.dto.ProductDTO;
import org.example.marketplacespringbootapp.model.Product;
import org.example.marketplacespringbootapp.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }

    // Any authenticated user can read
    @GetMapping
    public List<Product> list() { return service.list(); }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) { return service.get(id); }

    // Only ADMIN or MANAGER can create/update/delete
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Product create(@RequestBody @Valid ProductDTO dto) { return service.create(dto); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Product update(@PathVariable Long id, @RequestBody @Valid ProductDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public void delete(@PathVariable Long id) { service.delete(id); }
}

