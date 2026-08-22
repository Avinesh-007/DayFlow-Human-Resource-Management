package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.ApiResponse;
import com.dayflow.hrms.dto.attendance.AttendanceResponse;
import com.dayflow.hrms.dto.attendance.AttendanceUpdateRequest;
import com.dayflow.hrms.entity.AttendanceStatus;
import com.dayflow.hrms.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Attendance check-in/out and records")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    @Operation(summary = "Employee check-in")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Checked in successfully",
                attendanceService.checkIn(userDetails.getUsername())));
    }

    @PostMapping("/check-out")
    @Operation(summary = "Employee check-out")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkOut(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Checked out successfully",
                attendanceService.checkOut(userDetails.getUsername())));
    }

    @GetMapping("/me")
    @Operation(summary = "Get own attendance records")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getMyAttendance(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Attendance retrieved successfully",
                attendanceService.getMyAttendance(userDetails.getUsername(), date, startDate, endDate, pageable)));
    }

    @GetMapping
    @Operation(summary = "Admin: Get all attendance records")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getAllAttendance(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) AttendanceStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Attendance retrieved successfully",
                attendanceService.getAllAttendance(employeeId, date, startDate, endDate, status, pageable)));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Admin: Get attendance for a specific employee")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getAttendanceByEmployee(
            @PathVariable String employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Attendance retrieved successfully",
                attendanceService.getAttendanceByEmployee(employeeId, startDate, endDate, pageable)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Admin: Correct an attendance record")
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendance(
            @PathVariable Long id,
            @RequestBody AttendanceUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Attendance updated successfully",
                attendanceService.updateAttendance(id, request)));
    }
}
