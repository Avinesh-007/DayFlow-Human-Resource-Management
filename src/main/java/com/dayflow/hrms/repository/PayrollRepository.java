package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.entity.Payroll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    List<Payroll> findByEmployeeAndActiveTrue(Employee employee);

    Optional<Payroll> findTopByEmployeeAndActiveTrueOrderByEffectiveFromDesc(Employee employee);

    Page<Payroll> findByActiveTrue(Pageable pageable);

    Page<Payroll> findByEmployeeAndActiveTrue(Employee employee, Pageable pageable);
}
