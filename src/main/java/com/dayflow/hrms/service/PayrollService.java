package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.payroll.PayrollRequest;
import com.dayflow.hrms.dto.payroll.PayrollResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PayrollService {
    Page<PayrollResponse> getMyPayroll(String email, Pageable pageable);
    Page<PayrollResponse> getAllPayroll(Pageable pageable);
    Page<PayrollResponse> getPayrollByEmployee(String employeeId, Pageable pageable);
    PayrollResponse createPayroll(PayrollRequest request);
    PayrollResponse updatePayroll(Long id, PayrollRequest request);
    void deletePayroll(Long id);
}
