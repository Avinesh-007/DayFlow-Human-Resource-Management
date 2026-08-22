package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.employee.EmployeeRequest;
import com.dayflow.hrms.dto.employee.EmployeeResponse;
import com.dayflow.hrms.dto.employee.EmployeeUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    EmployeeResponse getMyProfile(String email);
    EmployeeResponse updateMyProfile(String email, EmployeeUpdateRequest request);
    Page<EmployeeResponse> getAllEmployees(String department, String employeeId, Pageable pageable);
    EmployeeResponse getEmployeeById(Long id);
    EmployeeResponse createEmployee(String employeeId, EmployeeRequest request);
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    void deleteEmployee(Long id);
}
