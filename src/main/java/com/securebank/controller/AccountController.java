package com.securebank.controller;

import com.securebank.dto.request.CreateAccountRequest;
import com.securebank.dto.response.AccountResponse;
import com.securebank.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Manages bank account operations for the authenticated user.
 * All endpoints require a valid JWT (enforced by SecurityConfig).
 *
 * @AuthenticationPrincipal injects the UserDetails of the current JWT holder —
 * this is how we know who the "current user" is without a session.
 */
@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Bank account management")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Creates a new SAVINGS or CURRENT account for the logged-in user.
     * POST /api/v1/accounts
     */
    @PostMapping
    @Operation(summary = "Create a bank account",
               description = "Creates a new account (SAVINGS or CURRENT) for the authenticated user")
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        AccountResponse response = accountService.createAccount(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created
    }

    /**
     * Returns all accounts belonging to the currently logged-in user.
     * GET /api/v1/accounts/my
     */
    @GetMapping("/my")
    @Operation(summary = "Get my accounts", description = "Returns all accounts owned by the authenticated user")
    public ResponseEntity<List<AccountResponse>> getMyAccounts(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(accountService.getMyAccounts(userDetails.getUsername()));
    }

    /**
     * Returns the current balance for a specific account.
     * Responds with 403 if the account does not belong to the logged-in user.
     * GET /api/v1/accounts/{id}/balance
     */
    @GetMapping("/{id}/balance")
    @Operation(summary = "Check balance",
               description = "Returns the current balance for the specified account (must be yours)")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(accountService.getBalance(id, userDetails.getUsername()));
    }
}
