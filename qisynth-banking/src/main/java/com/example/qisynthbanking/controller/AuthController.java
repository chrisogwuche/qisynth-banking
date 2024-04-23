package com.example.qisynthbanking.controller;

import com.example.qisynthbanking.dto.request.LoginRequest;
import com.example.qisynthbanking.dto.request.RefreshTokenRequest;
import com.example.qisynthbanking.dto.request.RegistrationRequest;
import com.example.qisynthbanking.dto.response.RegisterResponse;
import com.example.qisynthbanking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/v1/auth", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegistrationRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.getRefreshToken(request);
    }
}
