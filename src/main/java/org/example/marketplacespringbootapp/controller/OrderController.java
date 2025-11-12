package org.example.marketplacespringbootapp.controller;

import jakarta.validation.Valid;
import org.example.marketplacespringbootapp.dto.*;
import org.example.marketplacespringbootapp.model.*;
import org.example.marketplacespringbootapp.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Order Controller with Enhanced Security Annotations (Lab 6)
 *
 * Demonstrates custom authorization logic:
 * - Dynamic authorization based on user identity
 * - Role-based restrictions
 * - Admin-only operations
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service = service; }

    /**
     * LAB 6 - GET All Orders
     * Authorization: Any authenticated user
     * - ADMIN/MANAGER see ALL orders
     * - CUSTOMER sees only their own orders
     * Logic implemented in service layer
     */
    @GetMapping
    public List<Order> list(Authentication auth) {
        return service.listAll(auth);
    }

    /**
     * LAB 6 - POST Create Order
     * Authorization: Any authenticated user (ADMIN, MANAGER, CUSTOMER)
     * All users can create orders
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody @Valid CreateOrderDTO dto, Authentication auth) {
        return service.createOrder(dto, auth.getName());
    }

    /**
     * LAB 6 - GET Order by ID
     * Authorization: Any authenticated user
     * - ADMIN/MANAGER can view any order
     * - CUSTOMER can only view their own orders
     * Authorization logic in service layer
     */
    @GetMapping("/{id}")
    public Order get(@PathVariable Long id, Authentication auth) {
        return service.getOrder(id, auth);
    }

    /**
     * LAB 6 - DELETE Order (ADMIN or MANAGER only)
     * Authorization: ADMIN or MANAGER role required
     * Annotation: @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
     * Customers cannot delete orders
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public void delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth);
    }
}

