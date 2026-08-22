package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.ApiResponse;
import com.dayflow.hrms.dto.leave.LeaveResponse;
import com.dayflow.hrms.dto.leave.LeaveReviewRequest;
import com.dayflow.hrms.entity.LeaveStatus;
import com.dayflow.hrms.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/leaves")
@Tag(name = "Admin - Leave", description = "Admin leave approval and management")
public class AdminLeaveController {

    private final LeaveService leaveService;

    public AdminLeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping
    @Operation(summary = "Get all leave requests")
    public ResponseEntity<ApiResponse<Page<LeaveResponse>>> getAllLeaves(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Leave requests retrieved successfully",
                leaveService.getAllLeaves(pageable)));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending leave requests")
    public ResponseEntity<ApiResponse<Page<LeaveResponse>>> getPendingLeaves(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Pending leave requests retrieved successfully",
                leaveService.getLeavesByStatus(LeaveStatus.PENDING, pageable)));
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve a leave request")
    public ResponseEntity<ApiResponse<LeaveResponse>> approveLeave(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody LeaveReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Leave approved successfully",
                leaveService.approveLeave(userDetails.getUsername(), id, request)));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a leave request")
    public ResponseEntity<ApiResponse<LeaveResponse>> rejectLeave(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody LeaveReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Leave rejected successfully",
                leaveService.rejectLeave(userDetails.getUsername(), id, request)));
    }
}
