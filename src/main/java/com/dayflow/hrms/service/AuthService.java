package com.dayflow.hrms.service;

import com.dayflow.hrms.dto.auth.AuthResponse;
import com.dayflow.hrms.dto.auth.LoginRequest;
import com.dayflow.hrms.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    String verifyEmail(String token);
}
