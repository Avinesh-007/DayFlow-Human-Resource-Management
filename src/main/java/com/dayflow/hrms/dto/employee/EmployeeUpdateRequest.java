package com.dayflow.hrms.dto.employee;

import lombok.Data;

@Data
public class EmployeeUpdateRequest {
    private String phone;
    private String address;
    private String profilePictureUrl;
    private String emergencyContactName;
    private String emergencyContactPhone;
}
