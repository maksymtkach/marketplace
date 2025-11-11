package org.example.marketplacespringbootapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Security Configuration for Lab 4: CSRF Protection
 *
 * This configuration demonstrates Spring Security's CSRF (Cross-Site Request Forgery) protection.
 * CSRF protection is ENABLED by default in Spring Security and protects against unauthorized
 * state-changing requests from malicious websites.
 */
@Configuration
@EnableMethodSecurity // allows @PreAuthorize on controllers/services
public class SecurityConfig {

    /**
     * Defines in-memory users with different roles for testing authorization.
     *
     * Users:
     * - admin: Full access (ADMIN role)
     * - manager: Limited admin access (MANAGER role)
     * - alice: Regular user (CUSTOMER role)
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails manager = User.withUsername("manager")
                .password(encoder.encode("manager123"))
                .roles("MANAGER")
                .build();

        UserDetails alice = User.withUsername("alice")
                .password(encoder.encode("alice123"))
                .roles("CUSTOMER")
                .build();

        return new InMemoryUserDetailsManager(admin, manager, alice);
    }

    /**
     * Password encoder using BCrypt hashing algorithm.
     * BCrypt is a strong, adaptive hashing function designed for password storage.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Main security filter chain with CSRF protection enabled.
     *
     * CSRF Protection Strategy:
     * - CookieCsrfTokenRepository: Stores CSRF token in a cookie (default: XSRF-TOKEN)
     * - withHttpOnlyFalse(): Allows JavaScript to read the token (needed for AJAX requests)
     * - Token must be sent in request header (X-XSRF-TOKEN) or as request parameter (_csrf)
     *
     * Protection applies to state-changing methods: POST, PUT, DELETE, PATCH
     * Safe methods (GET, HEAD, OPTIONS, TRACE) don't require CSRF token
     *
     * Public endpoints:
     * - /actuator/health: Health check endpoint (no auth required)
     * - /csrf-token: CSRF token endpoint for Postman/frontend testing (no auth required)
     * - /api/v1/test/public: Public test endpoint (no auth required)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ===== CSRF PROTECTION CONFIGURATION (LAB 4) =====
                // Enable CSRF protection with cookie-based token repository
                // This is the recommended approach for browser-based applications
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // Cookie name: XSRF-TOKEN (default)
                        // Header name: X-XSRF-TOKEN (default)
                        // Parameter name: _csrf (default)
                )

                // ===== AUTHORIZATION RULES =====
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/csrf-token").permitAll()
                        .requestMatchers("/api/v1/test/public").permitAll()

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // ===== AUTHENTICATION METHOD =====
                // HTTP Basic Authentication (username + password in Authorization header)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}

