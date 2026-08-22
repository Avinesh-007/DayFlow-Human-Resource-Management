package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.Attendance;
import com.dayflow.hrms.entity.AttendanceStatus;
import com.dayflow.hrms.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeAndDate(Employee employee, LocalDate date);

    boolean existsByEmployeeAndDate(Employee employee, LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.employee = :employee AND " +
           "(:date IS NULL OR a.date = :date) AND " +
           "(:startDate IS NULL OR a.date >= :startDate) AND " +
           "(:endDate IS NULL OR a.date <= :endDate)")
    Page<Attendance> findByEmployeeWithFilters(@Param("employee") Employee employee,
                                               @Param("date") LocalDate date,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               Pageable pageable);

    @Query("SELECT a FROM Attendance a WHERE " +
           "(:employeeId IS NULL OR a.employee.employeeId = :employeeId) AND " +
           "(:date IS NULL OR a.date = :date) AND " +
           "(:startDate IS NULL OR a.date >= :startDate) AND " +
           "(:endDate IS NULL OR a.date <= :endDate) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Attendance> findAllWithFilters(@Param("employeeId") String employeeId,
                                        @Param("date") LocalDate date,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        @Param("status") AttendanceStatus status,
                                        Pageable pageable);

    long countByDateAndStatus(LocalDate date, AttendanceStatus status);

    List<Attendance> findByEmployeeAndDateBetween(Employee employee, LocalDate startDate, LocalDate endDate);
}
