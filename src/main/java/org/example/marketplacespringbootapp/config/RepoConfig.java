package org.example.marketplacespringbootapp.config;

import org.example.marketplacespringbootapp.repo.OrderRepository;
import org.example.marketplacespringbootapp.repo.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepoConfig {
    @Bean public ProductRepository productRepository() { return new ProductRepository(); }
    @Bean public OrderRepository orderRepository() { return new OrderRepository(); }
}

