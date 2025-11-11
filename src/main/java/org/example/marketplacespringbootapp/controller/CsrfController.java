package org.example.marketplacespringbootapp.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * CSRF Token Controller for Lab 4
 *
 * This controller provides an endpoint to retrieve the current CSRF token.
 * This is useful for testing with tools like Postman or for frontend applications
 * that need to obtain the CSRF token before making state-changing requests.
 *
 * The endpoint is publicly accessible (no authentication required) as configured
 * in SecurityConfig.java
 */
@RestController
public class CsrfController {

    /**
     * GET /csrf-token - Returns the current CSRF token in JSON format
     *
     * The CsrfToken is automatically injected by Spring Security when CSRF protection
     * is enabled. The token is stored in a cookie (XSRF-TOKEN) and must be sent in
     * subsequent POST/PUT/DELETE/PATCH requests either as:
     * - HTTP Header: X-XSRF-TOKEN: {token}
     * - Request Parameter: _csrf={token}
     *
     * Response example:
     * {
     *   "token": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
     *   "headerName": "X-XSRF-TOKEN",
     *   "parameterName": "_csrf"
     * }
     *
     * @param token the CSRF token automatically provided by Spring Security
     * @return Map containing token value and metadata
     */
    @GetMapping("/csrf-token")
    public Map<String, String> getCsrfToken(CsrfToken token) {
        Map<String, String> response = new HashMap<>();

        if (token != null) {
            response.put("token", token.getToken());
            response.put("headerName", token.getHeaderName());
            response.put("parameterName", token.getParameterName());
        } else {
            // This should not happen if CSRF is properly configured
            response.put("error", "CSRF token not available");
        }

        return response;
    }
}
