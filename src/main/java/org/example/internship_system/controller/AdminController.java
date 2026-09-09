package org.example.internship_system.controller;

import org.example.internship_system.dtos.response.AdminStatisticsResponse;
import org.example.internship_system.service.AdminStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminStatisticsService adminStatisticsService;

    public AdminController(AdminStatisticsService adminStatisticsService) {
        this.adminStatisticsService = adminStatisticsService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<AdminStatisticsResponse> getStatistics() {
        return ResponseEntity.ok(adminStatisticsService.getStatistics());
    }
}
