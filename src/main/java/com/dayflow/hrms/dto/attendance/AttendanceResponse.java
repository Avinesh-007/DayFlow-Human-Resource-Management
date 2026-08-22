package com.dayflow.hrms.dto.attendance;

import com.dayflow.hrms.entity.AttendanceStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class AttendanceResponse {
    private Long id;
    private String employeeId;
    private String employeeName;
    private LocalDate date;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private AttendanceStatus status;
    private BigDecimal workingHours;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
