package com.dayflow.hrms.service;

import com.dayflow.hrms.entity.*;
import com.dayflow.hrms.exception.BadRequestException;
import com.dayflow.hrms.repository.AttendanceRepository;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.repository.UserRepository;
import com.dayflow.hrms.service.impl.AttendanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock AttendanceRepository attendanceRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock UserRepository userRepository;

    @InjectMocks AttendanceServiceImpl attendanceService;

    private User user;
    private Employee employee;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("emp@test.com").role(Role.ROLE_EMPLOYEE).build();
        employee = Employee.builder().id(1L).user(user).employeeId("EMP001")
                .firstName("John").lastName("Doe").build();
        when(userRepository.findByEmail("emp@test.com")).thenReturn(Optional.of(user));
        when(employeeRepository.findByUser(user)).thenReturn(Optional.of(employee));
    }

    @Test
    void checkIn_success() {
        when(attendanceRepository.existsByEmployeeAndDate(eq(employee), any(LocalDate.class))).thenReturn(false);
        when(attendanceRepository.save(any())).thenAnswer(inv -> {
            Attendance a = inv.getArgument(0);
            a = Attendance.builder().id(1L).employee(a.getEmployee())
                    .date(a.getDate()).checkIn(a.getCheckIn()).status(a.getStatus()).build();
            return a;
        });

        var response = attendanceService.checkIn("emp@test.com");
        assertThat(response.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
        assertThat(response.getCheckIn()).isNotNull();
    }

    @Test
    void checkIn_duplicate_throws() {
        when(attendanceRepository.existsByEmployeeAndDate(eq(employee), any(LocalDate.class))).thenReturn(true);
        assertThatThrownBy(() -> attendanceService.checkIn("emp@test.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already checked in");
    }

    @Test
    void checkOut_success() {
        Attendance existing = Attendance.builder().id(1L).employee(employee)
                .date(LocalDate.now()).checkIn(LocalDateTime.now().minusHours(8))
                .status(AttendanceStatus.PRESENT).build();
        when(attendanceRepository.findByEmployeeAndDate(eq(employee), any(LocalDate.class)))
                .thenReturn(Optional.of(existing));
        when(attendanceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = attendanceService.checkOut("emp@test.com");
        assertThat(response.getCheckOut()).isNotNull();
        assertThat(response.getWorkingHours()).isNotNull();
    }

    @Test
    void checkOut_withoutCheckIn_throws() {
        when(attendanceRepository.findByEmployeeAndDate(eq(employee), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> attendanceService.checkOut("emp@test.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("No check-in found");
    }

    @Test
    void checkOut_alreadyCheckedOut_throws() {
        Attendance existing = Attendance.builder().id(1L).employee(employee)
                .date(LocalDate.now()).checkIn(LocalDateTime.now().minusHours(8))
                .checkOut(LocalDateTime.now().minusHours(1))
                .status(AttendanceStatus.PRESENT).build();
        when(attendanceRepository.findByEmployeeAndDate(eq(employee), any(LocalDate.class)))
                .thenReturn(Optional.of(existing));
        assertThatThrownBy(() -> attendanceService.checkOut("emp@test.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already checked out");
    }
}
