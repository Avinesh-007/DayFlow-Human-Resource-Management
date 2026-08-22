package com.dayflow.hrms.dto.dashboard;

import com.dayflow.hrms.dto.leave.LeaveResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @Builder
public class AdminDashboardResponse {
    private long totalEmployees;
    private long presentToday;
    private long absentToday;
    private long onLeaveToday;
    private long pendingLeaveRequests;
    private List<LeaveResponse> recentLeaveRequests;
}
