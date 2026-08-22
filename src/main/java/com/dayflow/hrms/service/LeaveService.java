package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.leave.LeaveApplicationRequest;
import com.dayflow.hrms.dto.leave.LeaveResponse;
import com.dayflow.hrms.dto.leave.LeaveReviewRequest;
import com.dayflow.hrms.entity.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaveService {
    LeaveResponse applyLeave(String email, LeaveApplicationRequest request);
    Page<LeaveResponse> getMyLeaves(String email, Pageable pageable);
    LeaveResponse getLeaveById(String email, Long id);
    Page<LeaveResponse> getAllLeaves(Pageable pageable);
    Page<LeaveResponse> getLeavesByStatus(LeaveStatus status, Pageable pageable);
    LeaveResponse approveLeave(String adminEmail, Long id, LeaveReviewRequest request);
    LeaveResponse rejectLeave(String adminEmail, Long id, LeaveReviewRequest request);
}
