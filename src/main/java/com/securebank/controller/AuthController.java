package com.securebank.controller;

import com.securebank.dto.request.LoginRequest;
import com.securebank.dto.request.RegisterRequest;
import com.securebank.dto.response.AuthResponse;
import com.securebank.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles public authentication endpoints.
 * No JWT required — these routes are explicitly permitted in SecurityConfig.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user and returns a JWT token.
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new account and returns a JWT token")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // @Valid triggers Bean Validation — invalid fields throw MethodArgumentNotValidException → 400
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created
    }

    /**
     * Authenticates a user and returns a JWT token.
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates credentials and returns a JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request)); // 200 OK
    }
}
