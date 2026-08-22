package com.dayflow.hrms.dto.leave;

import com.dayflow.hrms.entity.LeaveStatus;
import com.dayflow.hrms.entity.LeaveType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class LeaveResponse {
    private Long id;
    private String employeeId;
    private String employeeName;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfDays;
    private String remarks;
    private LeaveStatus status;
    private String adminComment;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
    private String reviewedBy;
}
