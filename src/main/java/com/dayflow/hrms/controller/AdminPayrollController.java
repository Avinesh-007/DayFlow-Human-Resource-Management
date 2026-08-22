package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.ApiResponse;
import com.dayflow.hrms.dto.payroll.PayrollRequest;
import com.dayflow.hrms.dto.payroll.PayrollResponse;
import com.dayflow.hrms.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/payroll")
@RequiredArgsConstructor
@Tag(name = "Admin - Payroll", description = "Admin payroll management")
public class AdminPayrollController {

    private final PayrollService payrollService;

    @GetMapping
    @Operation(summary = "Get all payroll records")
    public ResponseEntity<ApiResponse<Page<PayrollResponse>>> getAllPayroll(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Payroll records retrieved successfully",
                payrollService.getAllPayroll(pageable)));
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Get payroll for a specific employee")
    public ResponseEntity<ApiResponse<Page<PayrollResponse>>> getPayrollByEmployee(
            @PathVariable String employeeId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Payroll retrieved successfully",
                payrollService.getPayrollByEmployee(employeeId, pageable)));
    }

    @PostMapping
    @Operation(summary = "Create payroll for an employee")
    public ResponseEntity<ApiResponse<PayrollResponse>> createPayroll(@Valid @RequestBody PayrollRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payroll created successfully",
                        payrollService.createPayroll(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payroll record")
    public ResponseEntity<ApiResponse<PayrollResponse>> updatePayroll(
            @PathVariable Long id,
            @Valid @RequestBody PayrollRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Payroll updated successfully",
                payrollService.updatePayroll(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate payroll record")
    public ResponseEntity<ApiResponse<Void>> deletePayroll(@PathVariable Long id) {
        payrollService.deletePayroll(id);
        return ResponseEntity.ok(ApiResponse.success("Payroll deactivated successfully"));
    }
}
