package com.yusolbin.bio_os.controller;

import com.yusolbin.bio_os.dto.AdminDashboardResponse;
import com.yusolbin.bio_os.dto.AdminUserResponse;
import com.yusolbin.bio_os.security.CurrentUserService;
import com.yusolbin.bio_os.service.AdminDashboardService;
import com.yusolbin.bio_os.service.AdminUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;
    private final AdminUserService adminUserService;
    private final CurrentUserService currentUserService;

    public AdminDashboardController(
            AdminDashboardService adminDashboardService,
            AdminUserService adminUserService,
            CurrentUserService currentUserService
    ) {
        this.adminDashboardService = adminDashboardService;
        this.adminUserService = adminUserService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/summary")
    public AdminDashboardResponse getDashboardSummary() {
        requireAdmin();

        return adminDashboardService.getDashboardSummary();
    }

    @GetMapping("/users")
    public List<AdminUserResponse> getAdminUsers() {
        requireAdmin();

        return adminUserService.getUsers();
    }

    private void requireAdmin() {
        if (!currentUserService.isAdmin()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "ADMIN only"
            );
        }
    }
}