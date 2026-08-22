package com.dayflow.hrms.service.impl;

import com.dayflow.hrms.dto.payroll.PayrollRequest;
import com.dayflow.hrms.dto.payroll.PayrollResponse;
import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.entity.Payroll;
import com.dayflow.hrms.entity.User;
import com.dayflow.hrms.exception.ResourceNotFoundException;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.repository.PayrollRepository;
import com.dayflow.hrms.repository.UserRepository;
import com.dayflow.hrms.service.PayrollService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Override
    public Page<PayrollResponse> getMyPayroll(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        Employee employee = employeeRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found"));
        return payrollRepository.findByEmployeeAndActiveTrue(employee, pageable).map(this::toResponse);
    }

    @Override
    public Page<PayrollResponse> getAllPayroll(Pageable pageable) {
        return payrollRepository.findByActiveTrue(pageable).map(this::toResponse);
    }

    @Override
    public Page<PayrollResponse> getPayrollByEmployee(String employeeId, Pageable pageable) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
        return payrollRepository.findByEmployeeAndActiveTrue(employee, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public PayrollResponse createPayroll(PayrollRequest request) {
        Employee employee = employeeRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + request.getEmployeeId()));

        BigDecimal allowances = request.getAllowances() != null ? request.getAllowances() : BigDecimal.ZERO;
        BigDecimal deductions = request.getDeductions() != null ? request.getDeductions() : BigDecimal.ZERO;
        BigDecimal gross = request.getBasicSalary().add(allowances);
        BigDecimal net = gross.subtract(deductions);

        Payroll payroll = Payroll.builder()
                .employee(employee)
                .basicSalary(request.getBasicSalary())
                .allowances(allowances)
                .deductions(deductions)
                .grossSalary(gross)
                .netSalary(net)
                .effectiveFrom(request.getEffectiveFrom())
                .effectiveTo(request.getEffectiveTo())
                .build();

        log.info("Payroll created for employee: {}", request.getEmployeeId());
        return toResponse(payrollRepository.save(payroll));
    }

    @Override
    @Transactional
    public PayrollResponse updatePayroll(Long id, PayrollRequest request) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found: " + id));

        BigDecimal allowances = request.getAllowances() != null ? request.getAllowances() : BigDecimal.ZERO;
        BigDecimal deductions = request.getDeductions() != null ? request.getDeductions() : BigDecimal.ZERO;
        BigDecimal gross = request.getBasicSalary().add(allowances);
        BigDecimal net = gross.subtract(deductions);

        payroll.setBasicSalary(request.getBasicSalary());
        payroll.setAllowances(allowances);
        payroll.setDeductions(deductions);
        payroll.setGrossSalary(gross);
        payroll.setNetSalary(net);
        payroll.setEffectiveFrom(request.getEffectiveFrom());
        payroll.setEffectiveTo(request.getEffectiveTo());

        log.info("Payroll updated: {}", id);
        return toResponse(payrollRepository.save(payroll));
    }

    @Override
    @Transactional
    public void deletePayroll(Long id) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found: " + id));
        payroll.setActive(false);
        payrollRepository.save(payroll);
        log.info("Payroll deactivated: {}", id);
    }

    private PayrollResponse toResponse(Payroll p) {
        return PayrollResponse.builder()
                .id(p.getId())
                .employeeId(p.getEmployee().getEmployeeId())
                .employeeName(p.getEmployee().getFirstName() + " " + p.getEmployee().getLastName())
                .basicSalary(p.getBasicSalary())
                .allowances(p.getAllowances())
                .deductions(p.getDeductions())
                .grossSalary(p.getGrossSalary())
                .netSalary(p.getNetSalary())
                .effectiveFrom(p.getEffectiveFrom())
                .effectiveTo(p.getEffectiveTo())
                .active(p.isActive())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
