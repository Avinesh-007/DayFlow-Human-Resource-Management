package com.dayflow.hrms.config;

import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.entity.Role;
import com.dayflow.hrms.entity.User;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Seeds development data on startup.
 *
 * Development credentials (DO NOT use in production):
 *   Admin   — email: admin@dayflow.com    / password: Admin@123
 *   Employee— email: employee@dayflow.com / password: Employee@123
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedEmployee();
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail("admin@dayflow.com")) return;

        User admin = User.builder()
                .employeeId("ADM001")
                .email("admin@dayflow.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ROLE_ADMIN)
                .emailVerified(true)
                .build();
        userRepository.save(admin);

        Employee adminProfile = Employee.builder()
                .user(admin)
                .employeeId("ADM001")
                .firstName("Admin")
                .lastName("User")
                .email("admin@dayflow.com")
                .department("HR")
                .designation("HR Manager")
                .employmentType("FULL_TIME")
                .dateOfJoining(LocalDate.of(2020, 1, 1))
                .build();
        employeeRepository.save(adminProfile);

        log.info("Seeded admin user: admin@dayflow.com");
    }

    private void seedEmployee() {
        if (userRepository.existsByEmail("employee@dayflow.com")) return;

        User employee = User.builder()
                .employeeId("EMP001")
                .email("employee@dayflow.com")
                .password(passwordEncoder.encode("Employee@123"))
                .role(Role.ROLE_EMPLOYEE)
                .emailVerified(true)
                .build();
        userRepository.save(employee);

        Employee employeeProfile = Employee.builder()
                .user(employee)
                .employeeId("EMP001")
                .firstName("John")
                .lastName("Doe")
                .email("employee@dayflow.com")
                .department("Engineering")
                .designation("Software Engineer")
                .employmentType("FULL_TIME")
                .dateOfJoining(LocalDate.of(2023, 6, 1))
                .build();
        employeeRepository.save(employeeProfile);

        log.info("Seeded employee user: employee@dayflow.com");
    }
}
