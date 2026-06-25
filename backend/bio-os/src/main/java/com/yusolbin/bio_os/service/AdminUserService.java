package com.yusolbin.bio_os.service;

import com.yusolbin.bio_os.dto.AdminUserResponse;
import com.yusolbin.bio_os.model.UserAccount;
import com.yusolbin.bio_os.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class AdminUserService {

    private final UserAccountRepository userAccountRepository;

    public AdminUserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getUsers() {
        return userAccountRepository.findAllByOrderByIdAsc()
                .stream()
                .map(AdminUserResponse::new)
                .toList();
    }

    @Transactional
    public AdminUserResponse updateUserRole(
            Long targetUserId,
            String requestedRole,
            Long currentUserId
    ) {
        if (targetUserId.equals(currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot change your own role."
            );
        }

        String normalizedRole = normalizeRole(requestedRole);

        UserAccount targetUser = userAccountRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + targetUserId
                ));

        targetUser.setRole(normalizedRole);

        return new AdminUserResponse(targetUser);
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role is required."
            );
        }

        String normalizedRole = role.trim().toUpperCase(Locale.ROOT);

        if (!"ADMIN".equals(normalizedRole) && !"USER".equals(normalizedRole)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role must be ADMIN or USER."
            );
        }

        return normalizedRole;
    }
}