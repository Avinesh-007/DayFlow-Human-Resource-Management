package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.auth.LoginRequest;
import com.dayflow.hrms.dto.auth.RegisterRequest;
import com.dayflow.hrms.entity.Role;
import com.dayflow.hrms.entity.User;
import com.dayflow.hrms.exception.BadRequestException;
import com.dayflow.hrms.repository.UserRepository;
import com.dayflow.hrms.security.CustomUserDetailsService;
import com.dayflow.hrms.security.JwtService;
import com.dayflow.hrms.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @Mock AuthenticationManager authenticationManager;
    @Mock CustomUserDetailsService userDetailsService;

    @InjectMocks AuthServiceImpl authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmployeeId("EMP001");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password@123");
        registerRequest.setRole("EMPLOYEE");
    }

    @Test
    void register_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByEmployeeId(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u = User.builder().id(1L).employeeId(u.getEmployeeId())
                    .email(u.getEmail()).password(u.getPassword())
                    .role(u.getRole()).build();
            return u;
        });
        var userDetails = new org.springframework.security.core.userdetails.User(
                "test@example.com", "encoded", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        var response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void register_duplicateEmail_throws() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    void register_duplicateEmployeeId_throws() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByEmployeeId("EMP001")).thenReturn(true);
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Employee ID already exists");
    }

    @Test
    void login_success() {
        User user = User.builder().id(1L).employeeId("EMP001")
                .email("test@example.com").password("encoded")
                .role(Role.ROLE_EMPLOYEE).build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        var userDetails = new org.springframework.security.core.userdetails.User(
                "test@example.com", "encoded", List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("Password@123");

        var response = authService.login(loginRequest);
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getRole()).isEqualTo("ROLE_EMPLOYEE");
    }

    @Test
    void login_invalidPassword_throws() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrong");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }
}
