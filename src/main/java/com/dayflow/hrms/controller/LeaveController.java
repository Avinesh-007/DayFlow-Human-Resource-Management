package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.ApiResponse;
import com.dayflow.hrms.dto.leave.LeaveApplicationRequest;
import com.dayflow.hrms.dto.leave.LeaveResponse;
import com.dayflow.hrms.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@Tag(name = "Leave", description = "Employee leave management")
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping
    @Operation(summary = "Apply for leave")
    public ResponseEntity<ApiResponse<LeaveResponse>> applyLeave(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody LeaveApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Leave application submitted successfully",
                        leaveService.applyLeave(userDetails.getUsername(), request)));
    }

    @GetMapping("/me")
    @Operation(summary = "Get own leave requests")
    public ResponseEntity<ApiResponse<Page<LeaveResponse>>> getMyLeaves(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Leave requests retrieved successfully",
                leaveService.getMyLeaves(userDetails.getUsername(), pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific leave request")
    public ResponseEntity<ApiResponse<LeaveResponse>> getLeaveById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Leave request retrieved successfully",
                leaveService.getLeaveById(userDetails.getUsername(), id)));
    }
}
