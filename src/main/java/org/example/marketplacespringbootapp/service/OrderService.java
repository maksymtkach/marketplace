package org.example.marketplacespringbootapp.service;

import org.example.marketplacespringbootapp.dto.CreateOrderDTO;
import org.example.marketplacespringbootapp.dto.OrderItemDTO;
import org.example.marketplacespringbootapp.exception.NotFoundException;
import org.example.marketplacespringbootapp.exception.UnauthorizedException;
import org.example.marketplacespringbootapp.model.Order;
import org.example.marketplacespringbootapp.model.OrderItem;
import org.example.marketplacespringbootapp.model.Product;
import org.example.marketplacespringbootapp.repo.OrderRepository;
import org.example.marketplacespringbootapp.repo.ProductRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;

    public OrderService(OrderRepository orderRepo, ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    public Order createOrder(CreateOrderDTO dto, String username) {
        Order order = new Order(null, username);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemDTO itemDTO : dto.getItems()) {
            Product p = productRepo.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product " + itemDTO.getProductId() + " not found"));
            OrderItem item = new OrderItem(p.getId(), itemDTO.getQuantity(), p.getPrice());
            order.getItems().add(item);
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
        }
        order.setTotal(total);
        return orderRepo.save(order);
    }

    public Order getOrder(Long id, Authentication auth) {
        Order order = orderRepo.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        if (!isAdminOrManager(auth) && !order.getCustomerUsername().equals(auth.getName())) {
            throw new UnauthorizedException("You cannot access this order");
        }
        return order;
    }

    public List<Order> listAll(Authentication auth) {
        if (isAdminOrManager(auth)) return orderRepo.findAll();
        // else list only own orders
        return orderRepo.findAll().stream()
                .filter(o -> o.getCustomerUsername().equals(auth.getName()))
                .toList();
    }

    public void delete(Long id, Authentication auth) {
        Order order = getOrder(id, auth); // includes ownership/role check
        orderRepo.deleteById(order.getId());
    }

    private boolean isAdminOrManager(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));
    }
}

