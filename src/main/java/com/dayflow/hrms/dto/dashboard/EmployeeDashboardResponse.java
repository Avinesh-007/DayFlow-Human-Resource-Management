package com.dayflow.hrms.dto.dashboard;

import com.dayflow.hrms.dto.attendance.AttendanceResponse;
import com.dayflow.hrms.dto.employee.EmployeeResponse;
import com.dayflow.hrms.dto.leave.LeaveResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @Builder
public class EmployeeDashboardResponse {
    private EmployeeResponse profile;
    private AttendanceResponse todayAttendance;
    private String currentAttendanceStatus;
    private List<LeaveResponse> recentLeaveRequests;
    private long pendingLeaveCount;
}
