package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.dashboard.AdminDashboardResponse;
import com.dayflow.hrms.dto.dashboard.EmployeeDashboardResponse;

public interface DashboardService {
    EmployeeDashboardResponse getEmployeeDashboard(String email);
    AdminDashboardResponse getAdminDashboard();
}
