package com.securebank.service.impl;

import com.securebank.config.UserDetailsServiceImpl;
import com.securebank.dto.request.LoginRequest;
import com.securebank.dto.request.RegisterRequest;
import com.securebank.dto.response.AuthResponse;
import com.securebank.entity.User;
import com.securebank.exception.DuplicateAccountException;
import com.securebank.repository.UserRepository;
import com.securebank.service.AuthService;
import com.securebank.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles user registration and login.
 *
 * Registration flow:
 *   1. Check for duplicate email
 *   2. Hash password with BCrypt
 *   3. Save User entity
 *   4. Generate + return JWT
 *
 * Login flow:
 *   1. Delegate to Spring Security's AuthenticationManager (validates credentials)
 *   2. Load UserDetails for the authenticated user
 *   3. Generate + return JWT
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Registers a new user and returns a JWT token.
     *
     * @param request registration payload
     * @return AuthResponse with accessToken
     * @throws DuplicateAccountException if the email is already registered
     */
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Step 1: Reject duplicate emails immediately
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateAccountException(
                    "An account with email '" + request.getEmail() + "' already exists"
            );
        }

        // Step 2: Build and persist the User entity with a hashed password
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER);

        userRepository.save(user);
        log.info("Registered new user: {} ({})", user.getFullName(), user.getEmail());

        // Step 3: Generate JWT for the new user
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request login payload (email + password)
     * @return AuthResponse with accessToken
     * @throws org.springframework.security.authentication.BadCredentialsException on wrong credentials
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        // AuthenticationManager validates credentials; throws BadCredentialsException on failure
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // At this point, credentials are valid — load the full user for token generation
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(); // Can't be null — just authenticated

        log.info("User logged in: {}", user.getEmail());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}
