package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.ApiResponse;
import com.dayflow.hrms.dto.dashboard.AdminDashboardResponse;
import com.dayflow.hrms.dto.dashboard.EmployeeDashboardResponse;
import com.dayflow.hrms.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard summary APIs")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/employee")
    @Operation(summary = "Get employee dashboard data")
    public ResponseEntity<ApiResponse<EmployeeDashboardResponse>> getEmployeeDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved successfully",
                dashboardService.getEmployeeDashboard(userDetails.getUsername())));
    }

    @GetMapping("/admin")
    @Operation(summary = "Get admin dashboard data")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getAdminDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard data retrieved successfully",
                dashboardService.getAdminDashboard()));
    }
}
