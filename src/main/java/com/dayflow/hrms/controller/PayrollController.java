package com.dayflow.hrms.controller;

import com.dayflow.hrms.dto.ApiResponse;
import com.dayflow.hrms.dto.payroll.PayrollResponse;
import com.dayflow.hrms.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@Tag(name = "Payroll", description = "Employee payroll information")
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping("/me")
    @Operation(summary = "Get own payroll information")
    public ResponseEntity<ApiResponse<Page<PayrollResponse>>> getMyPayroll(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Payroll retrieved successfully",
                payrollService.getMyPayroll(userDetails.getUsername(), pageable)));
    }
}
