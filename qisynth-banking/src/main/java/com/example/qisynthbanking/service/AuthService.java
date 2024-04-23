package com.example.qisynthbanking.service;

import com.example.qisynthbanking.dto.request.LoginRequest;
import com.example.qisynthbanking.dto.request.RefreshTokenRequest;
import com.example.qisynthbanking.dto.request.RegistrationRequest;
import com.example.qisynthbanking.dto.response.RegisterResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    ResponseEntity<RegisterResponse> register(RegistrationRequest registrationRequest);
    ResponseEntity<?> login(LoginRequest request);
    ResponseEntity<?> getRefreshToken(RefreshTokenRequest request);
}
