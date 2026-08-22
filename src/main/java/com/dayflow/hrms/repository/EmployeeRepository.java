package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUser(User user);
    Optional<Employee> findByEmployeeId(String employeeId);
    Optional<Employee> findByEmail(String email);

    @Query("SELECT e FROM Employee e WHERE e.active = true AND " +
           "(:department IS NULL OR e.department = :department) AND " +
           "(:employeeId IS NULL OR e.employeeId LIKE %:employeeId%)")
    Page<Employee> findAllActive(@Param("department") String department,
                                  @Param("employeeId") String employeeId,
                                  Pageable pageable);

    long countByActiveTrue();
}
