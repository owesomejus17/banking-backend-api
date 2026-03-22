package com.securebank.controller;

import com.securebank.dto.request.DepositRequest;
import com.securebank.dto.request.TransferRequest;
import com.securebank.dto.request.WithdrawRequest;
import com.securebank.dto.response.PagedResponse;
import com.securebank.dto.response.TransactionResponse;
import com.securebank.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Handles all banking transaction operations.
 * All endpoints are secured via JWT (enforced by SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Deposit, withdrawal, transfer, and history")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Credits the specified account with the given amount.
     * POST /api/v1/transactions/deposit
     */
    @PostMapping("/deposit")
    @Operation(summary = "Deposit funds",
               description = "Credits an amount to the specified account. Account must be owned by the caller.")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody DepositRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(transactionService.deposit(request, userDetails.getUsername()));
    }

    /**
     * Debits the specified account by the given amount.
     * Returns 400 if balance is insufficient.
     * POST /api/v1/transactions/withdraw
     */
    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw funds",
               description = "Debits an amount from the specified account. Returns 400 if balance is insufficient.")
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid @RequestBody WithdrawRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(transactionService.withdraw(request, userDetails.getUsername()));
    }

    /**
     * Atomically transfers funds from one account to another.
     * Source account must be owned by the caller.
     * POST /api/v1/transactions/transfer
     */
    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds",
               description = "Atomically transfers an amount from one account to another. Caller must own the source account.")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(transactionService.transfer(request, userDetails.getUsername()));
    }

    /**
     * Returns paginated transaction history for the specified account.
     * GET /api/v1/transactions/{accountId}/history
     *
     * Query params:
     *   page      - zero-based page number (default: 0)
     *   size      - records per page (default: 10)
     *   sortBy    - field to sort by (default: timestamp)
     *   direction - ASC or DESC (default: DESC)
     */
    @GetMapping("/{accountId}/history")
    @Operation(summary = "Transaction history",
               description = "Returns paginated transaction history for the specified account.")
    public ResponseEntity<PagedResponse<TransactionResponse>> getHistory(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(transactionService.getTransactionHistory(
                accountId, userDetails.getUsername(), page, size, sortBy, direction));
    }
}
