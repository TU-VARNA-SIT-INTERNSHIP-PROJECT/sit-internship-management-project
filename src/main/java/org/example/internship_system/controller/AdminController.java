package org.example.internship_system.controller;

import org.example.internship_system.dtos.response.StatisticsResponse;
import org.example.internship_system.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final StatisticsService statisticsService;

    public AdminController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public StatisticsResponse getStatistics() {
        return statisticsService.getStatistics();
    }
}
