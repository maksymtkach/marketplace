package org.example.marketplacespringbootapp.controller;

import jakarta.validation.Valid;
import org.example.marketplacespringbootapp.dto.ProductDTO;
import org.example.marketplacespringbootapp.model.Product;
import org.example.marketplacespringbootapp.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Product Controller with Enhanced Security Annotations (Lab 6)
 *
 * Demonstrates various authorization patterns:
 * - hasRole() - Single role check
 * - hasAnyRole() - Multiple role check (OR)
 * - Authenticated users
 * - Admin-only operations
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }

    /**
     * LAB 6 - GET All Products
     * Authorization: Any authenticated user (ADMIN, MANAGER, CUSTOMER)
     * No @PreAuthorize annotation means requires authentication by default
     */
    @GetMapping
    public List<Product> list() { return service.list(); }

    /**
     * LAB 6 - GET Product by ID
     * Authorization: Any authenticated user
     */
    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) { return service.get(id); }

    /**
     * LAB 6 - POST Create Product
     * Authorization: ADMIN or MANAGER only
     * Annotation: @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Product create(@RequestBody @Valid ProductDTO dto) { return service.create(dto); }

    /**
     * LAB 6 - PUT Update Product
     * Authorization: ADMIN or MANAGER only
     * Annotation: @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Product update(@PathVariable Long id, @RequestBody @Valid ProductDTO dto) {
        return service.update(id, dto);
    }

    /**
     * LAB 6 - DELETE Product (ADMIN ONLY)
     * Authorization: ADMIN role only (more restrictive than create/update)
     * Annotation: @PreAuthorize("hasRole('ADMIN')")
     *
     * Note: This is different from Lab 5 - now only ADMIN can delete!
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { service.delete(id); }
}

