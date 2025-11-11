package org.example.marketplacespringbootapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Test Controller for Lab 4: CSRF Protection Demonstration
 *
 * This controller provides test endpoints to demonstrate CSRF protection behavior:
 * - Public GET endpoint (no auth, no CSRF token required)
 * - Protected POST endpoint (requires auth + CSRF token)
 * - Admin-only POST endpoint (requires ADMIN role + CSRF token)
 *
 * Expected behavior:
 * - GET requests: Work without CSRF token (safe methods)
 * - POST/PUT/DELETE/PATCH without CSRF token: Return 403 Forbidden
 * - POST/PUT/DELETE/PATCH with valid CSRF token: Work normally
 */
@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    /**
     * GET /api/v1/test/public - Public endpoint accessible without authentication
     *
     * This endpoint demonstrates that:
     * 1. GET requests don't require CSRF tokens (safe HTTP method)
     * 2. The endpoint is public (no authentication required)
     *
     * Expected result: Always returns 200 OK
     */
    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> publicEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a public endpoint - no authentication required");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("csrf_required", false);
        response.put("auth_required", false);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/test/protected - Protected endpoint requiring authentication
     *
     * This endpoint demonstrates that:
     * 1. GET requests don't require CSRF tokens (safe HTTP method)
     * 2. Authentication IS required (Basic Auth)
     *
     * Test:
     * - Without auth: 401 Unauthorized
     * - With auth: 200 OK
     */
    @GetMapping("/protected")
    public ResponseEntity<Map<String, Object>> protectedGet() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a protected GET endpoint");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("csrf_required", false);
        response.put("auth_required", true);
        response.put("note", "GET requests don't require CSRF tokens");

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/test/protected - Protected endpoint requiring authentication + CSRF token
     *
     * This endpoint demonstrates CSRF protection in action:
     * 1. POST requests REQUIRE a valid CSRF token
     * 2. Authentication IS required (Basic Auth)
     *
     * Test scenarios:
     * 1. No auth, no CSRF token: 401 Unauthorized
     * 2. With auth, no CSRF token: 403 Forbidden (CSRF protection blocks request)
     * 3. With auth, invalid CSRF token: 403 Forbidden
     * 4. With auth, valid CSRF token: 200 OK
     *
     * How to get CSRF token:
     * 1. Call GET /csrf-token to obtain the token
     * 2. Include token in request either as:
     *    - Header: X-XSRF-TOKEN: {token}
     *    - Parameter: _csrf={token}
     *
     * @param data Optional request body
     * @return Success message with timestamp
     */
    @PostMapping("/protected")
    public ResponseEntity<Map<String, Object>> protectedPost(@RequestBody(required = false) Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "POST request successful! CSRF token was valid.");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("csrf_required", true);
        response.put("auth_required", true);
        response.put("received_data", data != null ? data : "No data provided");

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/test/protected - Update endpoint requiring authentication + CSRF token
     *
     * Demonstrates CSRF protection on PUT requests.
     * Same behavior as POST - requires valid CSRF token.
     */
    @PutMapping("/protected")
    public ResponseEntity<Map<String, Object>> protectedPut(@RequestBody(required = false) Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "PUT request successful! CSRF token was valid.");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("csrf_required", true);
        response.put("auth_required", true);
        response.put("received_data", data != null ? data : "No data provided");

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/test/protected/{id} - Delete endpoint requiring authentication + CSRF token
     *
     * Demonstrates CSRF protection on DELETE requests.
     * Same behavior as POST - requires valid CSRF token.
     */
    @DeleteMapping("/protected/{id}")
    public ResponseEntity<Map<String, Object>> protectedDelete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "DELETE request successful! CSRF token was valid.");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("deleted_id", id);
        response.put("csrf_required", true);
        response.put("auth_required", true);

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/test/admin - Admin-only endpoint requiring ADMIN role + CSRF token
     *
     * This endpoint demonstrates:
     * 1. Role-based authorization (@PreAuthorize)
     * 2. CSRF protection (token required)
     * 3. Authentication required
     *
     * Test scenarios:
     * 1. No auth: 401 Unauthorized
     * 2. Auth as non-admin (manager/alice), no CSRF: 403 Forbidden (could be CSRF or role)
     * 3. Auth as non-admin, with CSRF: 403 Forbidden (insufficient role)
     * 4. Auth as admin, no CSRF: 403 Forbidden (CSRF protection)
     * 5. Auth as admin, with valid CSRF: 200 OK
     */
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> adminOnlyPost(@RequestBody(required = false) Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin POST successful! You have ADMIN role and provided valid CSRF token.");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("csrf_required", true);
        response.put("auth_required", true);
        response.put("role_required", "ADMIN");
        response.put("received_data", data != null ? data : "No data provided");

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/test/info - Information endpoint about CSRF protection
     *
     * Returns helpful information about how CSRF protection works in this application.
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> csrfInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("title", "CSRF Protection Information");

        Map<String, String> csrfDetails = new HashMap<>();
        csrfDetails.put("token_endpoint", "GET /csrf-token");
        csrfDetails.put("token_header", "X-XSRF-TOKEN");
        csrfDetails.put("token_parameter", "_csrf");
        csrfDetails.put("cookie_name", "XSRF-TOKEN");
        response.put("csrf_configuration", csrfDetails);

        Map<String, String> protectedMethods = new HashMap<>();
        protectedMethods.put("POST", "Requires CSRF token");
        protectedMethods.put("PUT", "Requires CSRF token");
        protectedMethods.put("DELETE", "Requires CSRF token");
        protectedMethods.put("PATCH", "Requires CSRF token");
        response.put("protected_methods", protectedMethods);

        Map<String, String> safeMethods = new HashMap<>();
        safeMethods.put("GET", "No CSRF token required");
        safeMethods.put("HEAD", "No CSRF token required");
        safeMethods.put("OPTIONS", "No CSRF token required");
        safeMethods.put("TRACE", "No CSRF token required");
        response.put("safe_methods", safeMethods);

        response.put("testing_steps", new String[]{
                "1. GET /csrf-token to obtain the token",
                "2. Copy the token value",
                "3. Add header 'X-XSRF-TOKEN: {token}' to your POST/PUT/DELETE/PATCH requests",
                "4. Try without token to see 403 Forbidden response",
                "5. Try with token to see 200 OK response"
        });

        return ResponseEntity.ok(response);
    }
}
