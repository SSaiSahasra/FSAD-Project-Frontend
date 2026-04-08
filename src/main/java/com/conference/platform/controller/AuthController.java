package com.conference.platform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.conference.platform.dto.ApiResponse;
import com.conference.platform.dto.AuthResponse;
import com.conference.platform.dto.GoogleAuthRequest;
import com.conference.platform.dto.LoginRequest;
import com.conference.platform.dto.RegisterRequest;
import com.conference.platform.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user login and registration")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                authService.register(request),
                "User registered successfully"
        ));
    }

    @Operation(summary = "Authenticate user and get JWT")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                authService.login(request),
                "Login successful"
        ));
    }

    @Operation(summary = "Authenticate or register user with Google")
    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleAuth(@Valid @RequestBody GoogleAuthRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                authService.authenticateWithGoogle(request.getIdToken()),
                "Google authentication successful"
        ));
    }
}
