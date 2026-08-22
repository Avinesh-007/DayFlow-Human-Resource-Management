package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.leave.LeaveApplicationRequest;
import com.dayflow.hrms.dto.leave.LeaveReviewRequest;
import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.exception.BadRequestException;
import com.dayflow.hrms.exception.UnauthorizedException;
import com.dayflow.hrms.repository.AttendanceRepository;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.repository.LeaveRequestRepository;
import com.dayflow.hrms.repository.UserRepository;
import com.dayflow.hrms.service.impl.LeaveServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock LeaveRequestRepository leaveRequestRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock UserRepository userRepository;
    @Mock AttendanceRepository attendanceRepository;

    LeaveServiceImpl leaveService;

    @BeforeEach
    void initService() {
        leaveService = new LeaveServiceImpl(leaveRequestRepository, employeeRepository, userRepository, attendanceRepository);
    }

    private User empUser;
    private User adminUser;
    private Employee employee;

    @BeforeEach
    void setUp() {
        empUser = User.builder().id(1L).email("emp@test.com").role(Role.ROLE_EMPLOYEE).build();
        adminUser = User.builder().id(2L).email("admin@test.com").role(Role.ROLE_ADMIN).build();
        employee = Employee.builder().id(1L).user(empUser).employeeId("EMP001")
                .firstName("John").lastName("Doe").build();
    }

    @Test
    void applyLeave_success() {
        when(userRepository.findByEmail("emp@test.com")).thenReturn(Optional.of(empUser));
        when(employeeRepository.findByUser(empUser)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.findOverlappingLeaves(any(), any(), any())).thenReturn(Collections.emptyList());
        when(leaveRequestRepository.save(any())).thenAnswer(inv -> {
            LeaveRequest lr = inv.getArgument(0);
            lr = LeaveRequest.builder().id(1L).employee(lr.getEmployee())
                    .leaveType(lr.getLeaveType()).startDate(lr.getStartDate())
                    .endDate(lr.getEndDate()).numberOfDays(lr.getNumberOfDays())
                    .status(LeaveStatus.PENDING).build();
            return lr;
        });

        LeaveApplicationRequest request = new LeaveApplicationRequest();
        request.setLeaveType(LeaveType.PAID);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));

        var response = leaveService.applyLeave("emp@test.com", request);
        assertThat(response.getStatus()).isEqualTo(LeaveStatus.PENDING);
        assertThat(response.getNumberOfDays()).isEqualTo(3);
    }

    @Test
    void applyLeave_invalidDateRange_throws() {
        when(userRepository.findByEmail("emp@test.com")).thenReturn(Optional.of(empUser));
        when(employeeRepository.findByUser(empUser)).thenReturn(Optional.of(employee));

        LeaveApplicationRequest request = new LeaveApplicationRequest();
        request.setLeaveType(LeaveType.PAID);
        request.setStartDate(LocalDate.now().plusDays(3));
        request.setEndDate(LocalDate.now().plusDays(1));

        assertThatThrownBy(() -> leaveService.applyLeave("emp@test.com", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Start date cannot be after end date");
    }

    @Test
    void applyLeave_overlapping_throws() {
        when(userRepository.findByEmail("emp@test.com")).thenReturn(Optional.of(empUser));
        when(employeeRepository.findByUser(empUser)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.findOverlappingLeaves(any(), any(), any()))
                .thenReturn(List.of(new LeaveRequest()));

        LeaveApplicationRequest request = new LeaveApplicationRequest();
        request.setLeaveType(LeaveType.PAID);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));

        assertThatThrownBy(() -> leaveService.applyLeave("emp@test.com", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("overlaps");
    }

    @Test
    void approveLeave_success() {
        LeaveRequest leave = LeaveRequest.builder().id(1L).employee(employee)
                .leaveType(LeaveType.PAID).startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(1)).numberOfDays(1)
                .status(LeaveStatus.PENDING).build();

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leave));
        when(attendanceRepository.findByEmployeeAndDate(any(), any())).thenReturn(Optional.empty());
        when(attendanceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(leaveRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeaveReviewRequest reviewRequest = new LeaveReviewRequest();
        reviewRequest.setComment("Approved");

        var response = leaveService.approveLeave("admin@test.com", 1L, reviewRequest);
        assertThat(response.getStatus()).isEqualTo(LeaveStatus.APPROVED);
    }

    @Test
    void approveLeave_alreadyApproved_throws() {
        LeaveRequest leave = LeaveRequest.builder().id(1L).employee(employee)
                .status(LeaveStatus.APPROVED).build();
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leave));

        assertThatThrownBy(() -> leaveService.approveLeave("admin@test.com", 1L, new LeaveReviewRequest()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only pending");
    }

    @Test
    void getLeaveById_otherEmployee_throws() {
        Employee otherEmployee = Employee.builder().id(2L).build();
        LeaveRequest leave = LeaveRequest.builder().id(1L).employee(otherEmployee)
                .status(LeaveStatus.PENDING).build();

        when(userRepository.findByEmail("emp@test.com")).thenReturn(Optional.of(empUser));
        when(employeeRepository.findByUser(empUser)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leave));

        assertThatThrownBy(() -> leaveService.getLeaveById("emp@test.com", 1L))
                .isInstanceOf(UnauthorizedException.class);
    }
}
