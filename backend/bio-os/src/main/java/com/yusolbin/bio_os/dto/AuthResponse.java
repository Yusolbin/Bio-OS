package com.yusolbin.bio_os.dto;

public class AuthResponse {

    private boolean success;
    private String message;
    private Long userId;
    private String username;
    private String role;
    private String token;

    public AuthResponse(
            boolean success,
            String message,
            Long userId,
            String username,
            String role
    ) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.token = null;
    }

    public AuthResponse(
            boolean success,
            String message,
            Long userId,
            String username,
            String role,
            String token
    ) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
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

    public String getToken() {
        return token;
    }
}