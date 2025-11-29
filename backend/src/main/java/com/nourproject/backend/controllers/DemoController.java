package com.nourproject.backend.controllers;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoController {

    /**
     * Public endpoint - no authentication required
     */
    @GetMapping("/public/hello")
    public Map<String, String> publicEndpoint() {
        return Map.of("message", "Hello from public endpoint!");
    }

    /**
     * Protected endpoint - requires authentication
     */
    @GetMapping("/user/profile")
    @PreAuthorize("hasRole('USER')") // Only users with USER role
    public Map<String, Object> userProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();

        return Map.of(
                "username", jwt.getClaim("preferred_username"),
                "email", jwt.getClaim("email"),
                "roles", auth.getAuthorities(),
                "message", "Hello " + jwt.getClaim("preferred_username")
        );
    }

    /**
     * Admin endpoint - requires ADMIN role
     */
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> adminDashboard() {
        return Map.of("message", "Welcome to admin dashboard!");
    }

    /**
     * Get current user info from JWT
     */
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getPrincipal() instanceof Jwt jwt) {
            return Map.of(
                    "username", jwt.getClaim("preferred_username"),
                    "email", jwt.getClaim("email"),
                    "firstName", jwt.getClaimAsString("given_name"),
                    "lastName", jwt.getClaimAsString("family_name"),
                    "roles", auth.getAuthorities()
            );
        }

        return Map.of("error", "Not authenticated");
    }
}
