package com.dayflow.hrms.dto.employee;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;
    private String address;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Date of joining is required")
    private LocalDate dateOfJoining;

    private String department;
    private String designation;
    private String employmentType;
    private String profilePictureUrl;
    private String bankAccountNumber;
    private String bankName;
    private String ifscCode;
    private String emergencyContactName;
    private String emergencyContactPhone;
}
