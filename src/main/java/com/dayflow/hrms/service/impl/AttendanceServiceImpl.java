package com.dayflow.hrms.service.impl;

import com.dayflow.hrms.dto.attendance.AttendanceResponse;
import com.dayflow.hrms.dto.attendance.AttendanceUpdateRequest;
import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.exception.BadRequestException;
import com.dayflow.hrms.exception.ResourceNotFoundException;
import com.dayflow.hrms.repository.AttendanceRepository;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.repository.UserRepository;
import com.dayflow.hrms.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AttendanceResponse checkIn(String email) {
        Employee employee = getEmployeeByEmail(email);
        LocalDate today = LocalDate.now();

        if (attendanceRepository.existsByEmployeeAndDate(employee, today)) {
            throw new BadRequestException("Employee has already checked in today");
        }

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .date(today)
                .checkIn(LocalDateTime.now())
                .status(AttendanceStatus.PRESENT)
                .build();

        log.info("Check-in recorded for employee: {}", employee.getEmployeeId());
        return toResponse(attendanceRepository.save(attendance));
    }

    @Override
    @Transactional
    public AttendanceResponse checkOut(String email) {
        Employee employee = getEmployeeByEmail(email);
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findByEmployeeAndDate(employee, today)
                .orElseThrow(() -> new BadRequestException("No check-in found for today"));

        if (attendance.getCheckOut() != null) {
            throw new BadRequestException("Employee has already checked out today");
        }

        LocalDateTime checkOut = LocalDateTime.now();
        attendance.setCheckOut(checkOut);

        Duration duration = Duration.between(attendance.getCheckIn(), checkOut);
        BigDecimal hours = BigDecimal.valueOf(duration.toMinutes())
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        attendance.setWorkingHours(hours);

        if (hours.compareTo(BigDecimal.valueOf(4)) < 0) {
            attendance.setStatus(AttendanceStatus.HALF_DAY);
        }

        log.info("Check-out recorded for employee: {}", employee.getEmployeeId());
        return toResponse(attendanceRepository.save(attendance));
    }

    @Override
    public Page<AttendanceResponse> getMyAttendance(String email, LocalDate date, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Employee employee = getEmployeeByEmail(email);
        return attendanceRepository.findByEmployeeWithFilters(employee, date, startDate, endDate, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<AttendanceResponse> getAllAttendance(String employeeId, LocalDate date, LocalDate startDate, LocalDate endDate, AttendanceStatus status, Pageable pageable) {
        return attendanceRepository.findAllWithFilters(employeeId, date, startDate, endDate, status, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<AttendanceResponse> getAttendanceByEmployee(String employeeId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
        return attendanceRepository.findByEmployeeWithFilters(employee, null, startDate, endDate, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public AttendanceResponse updateAttendance(Long id, AttendanceUpdateRequest request) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found: " + id));

        if (request.getCheckIn() != null) attendance.setCheckIn(request.getCheckIn());
        if (request.getCheckOut() != null) attendance.setCheckOut(request.getCheckOut());
        if (request.getStatus() != null) attendance.setStatus(request.getStatus());
        if (request.getRemarks() != null) attendance.setRemarks(request.getRemarks());

        if (attendance.getCheckIn() != null && attendance.getCheckOut() != null) {
            Duration duration = Duration.between(attendance.getCheckIn(), attendance.getCheckOut());
            attendance.setWorkingHours(BigDecimal.valueOf(duration.toMinutes())
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP));
        }

        log.info("Attendance updated by admin for record: {}", id);
        return toResponse(attendanceRepository.save(attendance));
    }

    private Employee getEmployeeByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return employeeRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for: " + email));
    }

    private AttendanceResponse toResponse(Attendance a) {
        return AttendanceResponse.builder()
                .id(a.getId())
                .employeeId(a.getEmployee().getEmployeeId())
                .employeeName(a.getEmployee().getFirstName() + " " + a.getEmployee().getLastName())
                .date(a.getDate())
                .checkIn(a.getCheckIn())
                .checkOut(a.getCheckOut())
                .status(a.getStatus())
                .workingHours(a.getWorkingHours())
                .remarks(a.getRemarks())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
