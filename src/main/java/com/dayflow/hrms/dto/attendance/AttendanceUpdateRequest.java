package com.dayflow.hrms.dto.attendance;

import com.dayflow.hrms.entity.AttendanceStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceUpdateRequest {
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private AttendanceStatus status;
    private String remarks;
}
