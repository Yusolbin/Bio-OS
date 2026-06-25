package com.yusolbin.bio_os.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public JwtUserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Authenticated user is required.");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof JwtUserPrincipal jwtUserPrincipal)) {
            throw new IllegalStateException("JWT principal is required.");
        }

        return jwtUserPrincipal;
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public String getCurrentRole() {
        return getCurrentUser().getRole();
    }

    public boolean isAdmin() {
        return "ADMIN".equals(getCurrentRole());
    }
}