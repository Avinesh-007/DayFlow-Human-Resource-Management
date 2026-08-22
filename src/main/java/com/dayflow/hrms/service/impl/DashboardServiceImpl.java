package com.dayflow.hrms.service.impl;

import com.dayflow.hrms.dto.attendance.AttendanceResponse;
import com.dayflow.hrms.dto.dashboard.AdminDashboardResponse;
import com.dayflow.hrms.dto.dashboard.EmployeeDashboardResponse;
import com.dayflow.hrms.dto.leave.LeaveResponse;
import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.exception.ResourceNotFoundException;
import com.dayflow.hrms.repository.*;
import com.dayflow.hrms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    public EmployeeDashboardResponse getEmployeeDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Employee employee = employeeRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found"));

        Optional<Attendance> todayAttendance = attendanceRepository.findByEmployeeAndDate(employee, LocalDate.now());

        String attendanceStatus = todayAttendance
                .map(a -> a.getStatus().name())
                .orElse("NOT_CHECKED_IN");

        AttendanceResponse todayAttendanceResponse = todayAttendance
                .map(a -> AttendanceResponse.builder()
                        .id(a.getId())
                        .employeeId(a.getEmployee().getEmployeeId())
                        .employeeName(a.getEmployee().getFirstName() + " " + a.getEmployee().getLastName())
                        .date(a.getDate())
                        .checkIn(a.getCheckIn())
                        .checkOut(a.getCheckOut())
                        .status(a.getStatus())
                        .workingHours(a.getWorkingHours())
                        .build())
                .orElse(null);

        List<LeaveResponse> recentLeaves = leaveRequestRepository
                .findTop5ByEmployeeOrderByAppliedAtDesc(employee)
                .stream()
                .map(lr -> LeaveResponse.builder()
                        .id(lr.getId())
                        .leaveType(lr.getLeaveType())
                        .startDate(lr.getStartDate())
                        .endDate(lr.getEndDate())
                        .numberOfDays(lr.getNumberOfDays())
                        .status(lr.getStatus())
                        .appliedAt(lr.getAppliedAt())
                        .build())
                .toList();

        long pendingCount = leaveRequestRepository.countByEmployeeAndStatus(employee, LeaveStatus.PENDING);

        return EmployeeDashboardResponse.builder()
                .profile(EmployeeServiceImpl.toResponse(employee))
                .todayAttendance(todayAttendanceResponse)
                .currentAttendanceStatus(attendanceStatus)
                .recentLeaveRequests(recentLeaves)
                .pendingLeaveCount(pendingCount)
                .build();
    }

    @Override
    public AdminDashboardResponse getAdminDashboard() {
        LocalDate today = LocalDate.now();

        long totalEmployees = employeeRepository.countByActiveTrue();
        long presentToday = attendanceRepository.countByDateAndStatus(today, AttendanceStatus.PRESENT);
        long onLeaveToday = attendanceRepository.countByDateAndStatus(today, AttendanceStatus.LEAVE);
        long absentToday = totalEmployees - presentToday - onLeaveToday;
        long pendingLeaves = leaveRequestRepository.findByStatus(LeaveStatus.PENDING,
                PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();

        List<LeaveResponse> recentLeaves = leaveRequestRepository
                .findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "appliedAt")))
                .stream()
                .map(lr -> LeaveResponse.builder()
                        .id(lr.getId())
                        .employeeId(lr.getEmployee().getEmployeeId())
                        .employeeName(lr.getEmployee().getFirstName() + " " + lr.getEmployee().getLastName())
                        .leaveType(lr.getLeaveType())
                        .startDate(lr.getStartDate())
                        .endDate(lr.getEndDate())
                        .numberOfDays(lr.getNumberOfDays())
                        .status(lr.getStatus())
                        .appliedAt(lr.getAppliedAt())
                        .build())
                .toList();

        return AdminDashboardResponse.builder()
                .totalEmployees(totalEmployees)
                .presentToday(presentToday)
                .absentToday(Math.max(absentToday, 0))
                .onLeaveToday(onLeaveToday)
                .pendingLeaveRequests(pendingLeaves)
                .recentLeaveRequests(recentLeaves)
                .build();
    }
}
