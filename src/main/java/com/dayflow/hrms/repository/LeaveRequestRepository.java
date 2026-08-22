package com.dayflow.hrms.repository;

import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.entity.LeaveRequest;
import com.dayflow.hrms.entity.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    Page<LeaveRequest> findByEmployee(Employee employee, Pageable pageable);

    Page<LeaveRequest> findByStatus(LeaveStatus status, Pageable pageable);

    Page<LeaveRequest> findAll(Pageable pageable);

    long countByEmployeeAndStatus(Employee employee, LeaveStatus status);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee = :employee AND " +
           "lr.status != 'REJECTED' AND " +
           "(:startDate <= lr.endDate AND :endDate >= lr.startDate)")
    List<LeaveRequest> findOverlappingLeaves(@Param("employee") Employee employee,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    List<LeaveRequest> findByEmployeeAndStatusAndStartDateBetween(Employee employee,
                                                                   LeaveStatus status,
                                                                   LocalDate startDate,
                                                                   LocalDate endDate);

    List<LeaveRequest> findTop5ByEmployeeOrderByAppliedAtDesc(Employee employee);
}
