package org.example.marketplacespringbootapp.config;

import jakarta.annotation.PostConstruct;
import org.example.marketplacespringbootapp.model.Product;
import org.example.marketplacespringbootapp.repo.ProductRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Data Initializer - Populates sample data on application startup
 *
 * This component runs after the application context is loaded and adds
 * sample products to demonstrate role-based access control.
 */
@Component
public class DataInitializer {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Initialize sample data when application starts
     */
    @PostConstruct
    public void init() {
        System.out.println("=== Initializing Sample Data ===");

        // Add sample products
        Product laptop = new Product(null, "TECH-001", "Laptop Dell XPS 15",
                new BigDecimal("1299.99"), 10);
        productRepository.save(laptop);

        Product mouse = new Product(null, "TECH-002", "Wireless Mouse Logitech MX",
                new BigDecimal("79.99"), 50);
        productRepository.save(mouse);

        Product keyboard = new Product(null, "TECH-003", "Mechanical Keyboard Keychron K2",
                new BigDecimal("89.99"), 30);
        productRepository.save(keyboard);

        Product monitor = new Product(null, "TECH-004", "Monitor LG 27 inch 4K",
                new BigDecimal("399.99"), 15);
        productRepository.save(monitor);

        Product headphones = new Product(null, "TECH-005", "Headphones Sony WH-1000XM5",
                new BigDecimal("349.99"), 25);
        productRepository.save(headphones);

        System.out.println("=== Sample Data Initialized: 5 Products Created ===");
    }
}
