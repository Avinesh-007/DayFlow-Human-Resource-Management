package com.dayflow.hrms.service.impl;

import com.dayflow.hrms.dto.auth.AuthResponse;
import com.dayflow.hrms.dto.auth.LoginRequest;
import com.dayflow.hrms.dto.auth.RegisterRequest;
import com.dayflow.hrms.entity.Employee;
import com.dayflow.hrms.entity.Role;
import com.dayflow.hrms.entity.User;
import com.dayflow.hrms.exception.BadRequestException;
import com.dayflow.hrms.exception.ResourceNotFoundException;
import com.dayflow.hrms.repository.EmployeeRepository;
import com.dayflow.hrms.repository.UserRepository;
import com.dayflow.hrms.security.CustomUserDetailsService;
import com.dayflow.hrms.security.JwtService;
import com.dayflow.hrms.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;


    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                    "Email already registered: " + request.getEmail()
            );
        }

        if (userRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new BadRequestException(
                    "Employee ID already exists: " + request.getEmployeeId()
            );
        }

        Role role;

        try {
            role = Role.valueOf(
                    "ROLE_" + request.getRole().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Invalid role: " + request.getRole()
            );
        }


        // Create User

        User user = User.builder()
                .employeeId(request.getEmployeeId())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(true)
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .build();

        userRepository.save(user);

        log.info("User registered: {}", user.getEmail());


        // Create Employee Profile

        if (role == Role.ROLE_EMPLOYEE) {

            Employee employee = Employee.builder()
                    .user(user)
                    .employeeId(user.getEmployeeId())
                    .firstName("New")
                    .lastName("Employee")
                    .email(user.getEmail())
                    .dateOfJoining(LocalDate.now())
                    .active(true)
                    .build();

            employeeRepository.save(employee);

            log.info(
                    "Employee profile created for: {}",
                    employee.getEmail()
            );
        }


        // Generate JWT

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        String token = jwtService.generateToken(userDetails);


        // Response

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .employeeId(user.getEmployeeId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }


    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        String token = jwtService.generateToken(userDetails);

        log.info("User logged in: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .employeeId(user.getEmployeeId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }


    @Override
    @Transactional
    public String verifyEmail(String token) {

        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Invalid or expired verification token"
                        )
                );

        user.setEmailVerified(true);
        user.setVerificationToken(null);

        userRepository.save(user);

        log.info(
                "Email verified for user: {}",
                user.getEmail()
        );

        return "Email verified successfully";
    }
}