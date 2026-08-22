package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.ApiResponse;
import com.dayflow.hrms.dto.employee.EmployeeRequest;
import com.dayflow.hrms.dto.employee.EmployeeResponse;
import com.dayflow.hrms.dto.employee.EmployeeUpdateRequest;
import com.dayflow.hrms.service.EmployeeService;
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
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Employee profile management")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/me")
    @Operation(summary = "Get authenticated employee's profile")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully",
                employeeService.getMyProfile(userDetails.getUsername())));
    }

    @PutMapping("/me")
    @Operation(summary = "Update own profile (restricted fields)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully",
                employeeService.updateMyProfile(userDetails.getUsername(), request)));
    }

    @GetMapping
    @Operation(summary = "Admin: Get all employees")
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getAllEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String employeeId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully",
                employeeService.getAllEmployees(department, employeeId, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Admin: Get employee by ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Employee retrieved successfully",
                employeeService.getEmployeeById(id)));
    }

    @PostMapping
    @Operation(summary = "Admin: Create employee profile (requires existing user with employeeId)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @RequestParam String employeeId,
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully",
                        employeeService.createEmployee(employeeId, request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Admin: Update employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully",
                employeeService.updateEmployee(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Admin: Deactivate employee")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deactivated successfully"));
    }
}
