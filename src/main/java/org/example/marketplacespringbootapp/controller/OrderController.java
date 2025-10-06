package org.example.marketplacespringbootapp.controller;

import jakarta.validation.Valid;
import org.example.marketplacespringbootapp.dto.*;
import org.example.marketplacespringbootapp.model.*;
import org.example.marketplacespringbootapp.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service = service; }

    // List: admins/managers see all; customers see their own
    @GetMapping
    public List<Order> list(Authentication auth) {
        return service.listAll(auth);
    }

    // Create: any authenticated user (customer creates own)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody @Valid CreateOrderDTO dto, Authentication auth) {
        return service.createOrder(dto, auth.getName());
    }

    // Get by id: owner or admin/manager
    @GetMapping("/{id}")
    public Order get(@PathVariable Long id, Authentication auth) {
        return service.getOrder(id, auth);
    }

    // Delete: owner or admin/manager
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth);
    }
}

