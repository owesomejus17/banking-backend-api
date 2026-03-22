package com.securebank.service;

import com.securebank.dto.request.LoginRequest;
import com.securebank.dto.request.RegisterRequest;
import com.securebank.dto.response.AuthResponse;

/**
 * Defines the contract for authentication operations.
 */
public interface AuthService {

    /**
     * Registers a new user with the given details.
     * Throws {@link com.securebank.exception.DuplicateAccountException} if email is already in use.
     *
     * @param request registration data (fullName, email, password)
     * @return AuthResponse containing the JWT access token
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a user with email and password.
     * Throws {@link org.springframework.security.authentication.BadCredentialsException} if credentials are invalid.
     *
     * @param request login data (email, password)
     * @return AuthResponse containing the JWT access token
     */
    AuthResponse login(LoginRequest request);
}
