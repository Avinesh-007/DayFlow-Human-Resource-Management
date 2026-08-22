package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.attendance.AttendanceResponse;
import com.dayflow.hrms.dto.attendance.AttendanceUpdateRequest;
import com.dayflow.hrms.entity.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AttendanceService {
    AttendanceResponse checkIn(String email);
    AttendanceResponse checkOut(String email);
    Page<AttendanceResponse> getMyAttendance(String email, LocalDate date, LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<AttendanceResponse> getAllAttendance(String employeeId, LocalDate date, LocalDate startDate, LocalDate endDate, AttendanceStatus status, Pageable pageable);
    Page<AttendanceResponse> getAttendanceByEmployee(String employeeId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    AttendanceResponse updateAttendance(Long id, AttendanceUpdateRequest request);
}
