package com.dayflow.hrms.service.impl;

import com.dayflow.hrms.dto.leave.LeaveApplicationRequest;
import com.dayflow.hrms.dto.leave.LeaveResponse;
import com.dayflow.hrms.dto.leave.LeaveReviewRequest;
import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.exception.BadRequestException;
import com.dayflow.hrms.exception.ResourceNotFoundException;
import com.dayflow.hrms.exception.UnauthorizedException;
import com.dayflow.hrms.repository.*;
import com.dayflow.hrms.service.LeaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    @Transactional
    public LeaveResponse applyLeave(String email, LeaveApplicationRequest request) {
        Employee employee = getEmployeeByEmail(email);

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Start date cannot be after end date");
        }

        List<LeaveRequest> overlapping = leaveRequestRepository.findOverlappingLeaves(
                employee, request.getStartDate(), request.getEndDate());
        if (!overlapping.isEmpty()) {
            throw new BadRequestException("Leave request overlaps with an existing leave");
        }

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(employee)
                .leaveType(request.getLeaveType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .numberOfDays((int) days)
                .remarks(request.getRemarks())
                .status(LeaveStatus.PENDING)
                .build();

        log.info("Leave applied by employee: {} for {} days", employee.getEmployeeId(), days);
        return toResponse(leaveRequestRepository.save(leaveRequest));
    }

    @Override
    public Page<LeaveResponse> getMyLeaves(String email, Pageable pageable) {
        Employee employee = getEmployeeByEmail(email);
        return leaveRequestRepository.findByEmployee(employee, pageable).map(this::toResponse);
    }

    @Override
    public LeaveResponse getLeaveById(String email, Long id) {
        LeaveRequest leave = findById(id);
        Employee employee = getEmployeeByEmail(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;
        if (!isAdmin && !leave.getEmployee().getId().equals(employee.getId())) {
            throw new UnauthorizedException("Access denied to this leave request");
        }
        return toResponse(leave);
    }

    @Override
    public Page<LeaveResponse> getAllLeaves(Pageable pageable) {
        return leaveRequestRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public Page<LeaveResponse> getLeavesByStatus(LeaveStatus status, Pageable pageable) {
        return leaveRequestRepository.findByStatus(status, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public LeaveResponse approveLeave(String adminEmail, Long id, LeaveReviewRequest request) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        LeaveRequest leave = findById(id);

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Only pending leave requests can be approved");
        }

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setAdminComment(request.getComment());
        leave.setReviewedAt(LocalDateTime.now());
        leave.setReviewedBy(admin);

        markAttendanceAsLeave(leave);

        log.info("Leave {} approved by admin: {}", id, adminEmail);
        return toResponse(leaveRequestRepository.save(leave));
    }

    @Override
    @Transactional
    public LeaveResponse rejectLeave(String adminEmail, Long id, LeaveReviewRequest request) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        LeaveRequest leave = findById(id);

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Only pending leave requests can be rejected");
        }

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setAdminComment(request.getComment());
        leave.setReviewedAt(LocalDateTime.now());
        leave.setReviewedBy(admin);

        log.info("Leave {} rejected by admin: {}", id, adminEmail);
        return toResponse(leaveRequestRepository.save(leave));
    }

    private void markAttendanceAsLeave(LeaveRequest leave) {
        LocalDate current = leave.getStartDate();
        while (!current.isAfter(leave.getEndDate())) {
            final LocalDate date = current;
            Attendance attendance = attendanceRepository
                    .findByEmployeeAndDate(leave.getEmployee(), date)
                    .orElseGet(() -> Attendance.builder()
                            .employee(leave.getEmployee())
                            .date(date)
                            .status(AttendanceStatus.LEAVE)
                            .build());
            attendance.setStatus(AttendanceStatus.LEAVE);
            attendance.setRemarks("Approved leave: " + leave.getLeaveType());
            attendanceRepository.save(attendance);
            current = current.plusDays(1);
        }
    }

    private LeaveRequest findById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + id));
    }

    private Employee getEmployeeByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return employeeRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for: " + email));
    }

    private LeaveResponse toResponse(LeaveRequest lr) {
        return LeaveResponse.builder()
                .id(lr.getId())
                .employeeId(lr.getEmployee().getEmployeeId())
                .employeeName(lr.getEmployee().getFirstName() + " " + lr.getEmployee().getLastName())
                .leaveType(lr.getLeaveType())
                .startDate(lr.getStartDate())
                .endDate(lr.getEndDate())
                .numberOfDays(lr.getNumberOfDays())
                .remarks(lr.getRemarks())
                .status(lr.getStatus())
                .adminComment(lr.getAdminComment())
                .appliedAt(lr.getAppliedAt())
                .reviewedAt(lr.getReviewedAt())
                .reviewedBy(lr.getReviewedBy() != null ? lr.getReviewedBy().getEmail() : null)
                .build();
    }
}
