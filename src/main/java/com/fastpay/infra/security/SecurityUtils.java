package com.fastpay.infra.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    public static String extractEmail(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Authentication context is empty");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof String email) {
            return email;
        }

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        try {
            return (String) principal.getClass().getMethod("getEmail").invoke(principal);
        } catch (Exception e) {
            throw new IllegalStateException("Could not extract email from principal object: " + principal.getClass().getName());
        }
    }
}