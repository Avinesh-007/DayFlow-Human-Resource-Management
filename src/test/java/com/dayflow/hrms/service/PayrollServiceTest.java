package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.payroll.PayrollRequest;
import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.exception.ResourceNotFoundException;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.repository.PayrollRepository;
import com.dayflow.hrms.repository.UserRepository;
import com.dayflow.hrms.service.impl.PayrollServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock PayrollRepository payrollRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock UserRepository userRepository;

    @InjectMocks PayrollServiceImpl payrollService;

    private User user;
    private Employee employee;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("emp@test.com").role(Role.ROLE_EMPLOYEE).build();
        employee = Employee.builder().id(1L).user(user).employeeId("EMP001")
                .firstName("John").lastName("Doe").build();
    }

    @Test
    void getMyPayroll_success() {
        Payroll payroll = Payroll.builder().id(1L).employee(employee)
                .basicSalary(new BigDecimal("50000")).allowances(new BigDecimal("5000"))
                .deductions(new BigDecimal("2000")).grossSalary(new BigDecimal("55000"))
                .netSalary(new BigDecimal("53000")).effectiveFrom(LocalDate.now()).active(true).build();

        when(userRepository.findByEmail("emp@test.com")).thenReturn(Optional.of(user));
        when(employeeRepository.findByUser(user)).thenReturn(Optional.of(employee));
        when(payrollRepository.findByEmployeeAndActiveTrue(eq(employee), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(payroll)));

        var result = payrollService.getMyPayroll("emp@test.com", Pageable.unpaged());
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNetSalary()).isEqualByComparingTo("53000");
    }

    @Test
    void createPayroll_calculatesCorrectly() {
        when(employeeRepository.findByEmployeeId("EMP001")).thenReturn(Optional.of(employee));
        when(payrollRepository.save(any())).thenAnswer(inv -> {
            Payroll p = inv.getArgument(0);
            p = Payroll.builder().id(1L).employee(p.getEmployee())
                    .basicSalary(p.getBasicSalary()).allowances(p.getAllowances())
                    .deductions(p.getDeductions()).grossSalary(p.getGrossSalary())
                    .netSalary(p.getNetSalary()).effectiveFrom(p.getEffectiveFrom())
                    .active(true).build();
            return p;
        });

        PayrollRequest request = new PayrollRequest();
        request.setEmployeeId("EMP001");
        request.setBasicSalary(new BigDecimal("50000"));
        request.setAllowances(new BigDecimal("5000"));
        request.setDeductions(new BigDecimal("2000"));
        request.setEffectiveFrom(LocalDate.now());

        var response = payrollService.createPayroll(request);
        assertThat(response.getGrossSalary()).isEqualByComparingTo("55000");
        assertThat(response.getNetSalary()).isEqualByComparingTo("53000");
    }

    @Test
    void createPayroll_employeeNotFound_throws() {
        when(employeeRepository.findByEmployeeId("INVALID")).thenReturn(Optional.empty());

        PayrollRequest request = new PayrollRequest();
        request.setEmployeeId("INVALID");
        request.setBasicSalary(new BigDecimal("50000"));
        request.setEffectiveFrom(LocalDate.now());

        assertThatThrownBy(() -> payrollService.createPayroll(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
