package com.yusolbin.bio_os.controller;

import com.yusolbin.bio_os.dto.AdminDashboardResponse;
import com.yusolbin.bio_os.service.AdminDashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/summary")
    public AdminDashboardResponse getDashboardSummary() {
        return adminDashboardService.getDashboardSummary();
    }
}