package com.vn.movie_flix.controller;

import com.vn.movie_flix.dto.response.DashboardStatsDto;
import com.vn.movie_flix.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/admin")
    public String showDashboard(Model model) {
        DashboardStatsDto stats = dashboardService.getDashboardStats();

        model.addAttribute("title", "Bảng điều khiển");
        model.addAttribute("stats", stats);

        return "/admin/dashboard";
    }
}
