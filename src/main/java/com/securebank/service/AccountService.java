package com.securebank.service;

import com.securebank.dto.request.CreateAccountRequest;
import com.securebank.dto.response.AccountResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Defines the contract for bank account operations.
 */
public interface AccountService {

    /**
     * Creates a new bank account for the currently authenticated user.
     *
     * @param request       account creation details (type)
     * @param userEmail     email of the authenticated user (from JWT)
     * @return AccountResponse with the newly created account details
     */
    AccountResponse createAccount(CreateAccountRequest request, String userEmail);

    /**
     * Returns all accounts belonging to the currently authenticated user.
     *
     * @param userEmail email of the authenticated user
     * @return list of AccountResponse DTOs
     */
    List<AccountResponse> getMyAccounts(String userEmail);

    /**
     * Retrieves the current balance of the specified account.
     * Throws {@link org.springframework.security.access.AccessDeniedException} if
     * the authenticated user does not own this account.
     *
     * @param accountId UUID of the account
     * @param userEmail email of the authenticated user
     * @return current balance as BigDecimal
     */
    BigDecimal getBalance(UUID accountId, String userEmail);
}
