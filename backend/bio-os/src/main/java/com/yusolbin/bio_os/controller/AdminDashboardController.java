package com.yusolbin.bio_os.controller;

import com.yusolbin.bio_os.dto.AdminDashboardResponse;
import com.yusolbin.bio_os.security.CurrentUserService;
import com.yusolbin.bio_os.service.AdminDashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;
    private final CurrentUserService currentUserService;

    public AdminDashboardController(
            AdminDashboardService adminDashboardService,
            CurrentUserService currentUserService
    ) {
        this.adminDashboardService = adminDashboardService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/summary")
    public AdminDashboardResponse getDashboardSummary() {
        requireAdmin();

        return adminDashboardService.getDashboardSummary();
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