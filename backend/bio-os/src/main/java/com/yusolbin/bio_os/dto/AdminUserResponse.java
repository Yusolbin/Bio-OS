package com.yusolbin.bio_os.dto;

import com.yusolbin.bio_os.model.UserAccount;

import java.time.LocalDateTime;

public class AdminUserResponse {

    private Long userId;
    private String username;
    private String role;
    private LocalDateTime createdAt;

    public AdminUserResponse(UserAccount userAccount) {
        this.userId = userAccount.getId();
        this.username = userAccount.getUsername();
        this.role = userAccount.getRole();
        this.createdAt = userAccount.getCreatedAt();
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}