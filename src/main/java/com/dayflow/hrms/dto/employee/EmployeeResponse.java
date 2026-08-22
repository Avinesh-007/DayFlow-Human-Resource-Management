package com.dayflow.hrms.dto.employee;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder
public class EmployeeResponse {
    private Long id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private LocalDate dateOfJoining;
    private String department;
    private String designation;
    private String employmentType;
    private String profilePictureUrl;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
