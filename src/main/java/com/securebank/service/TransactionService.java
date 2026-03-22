package com.securebank.service;

import com.securebank.dto.request.DepositRequest;
import com.securebank.dto.request.TransferRequest;
import com.securebank.dto.request.WithdrawRequest;
import com.securebank.dto.response.PagedResponse;
import com.securebank.dto.response.TransactionResponse;

import java.util.UUID;

/**
 * Defines the contract for all banking transaction operations.
 * All mutating operations are transactional — they either complete fully or roll back.
 */
public interface TransactionService {

    /**
     * Credits the specified account with the given amount.
     * Always saves a transaction record (SUCCESS status).
     *
     * @param request   deposit details (accountId, amount, description)
     * @param userEmail email of the authenticated user (for ownership check)
     * @return TransactionResponse record of the deposit
     */
    TransactionResponse deposit(DepositRequest request, String userEmail);

    /**
     * Debits the specified account by the given amount.
     * Throws {@link com.securebank.exception.InsufficientFundsException} if balance is insufficient.
     * Saves a FAILED transaction record before throwing on insufficient funds.
     *
     * @param request   withdrawal details (accountId, amount, description)
     * @param userEmail email of the authenticated user (for ownership check)
     * @return TransactionResponse record of the withdrawal
     */
    TransactionResponse withdraw(WithdrawRequest request, String userEmail);

    /**
     * Atomically debits the source account and credits the destination account.
     * Both operations happen in one @Transactional boundary — either both succeed or both roll back.
     * Throws {@link com.securebank.exception.InsufficientFundsException} if source balance is insufficient.
     *
     * @param request   transfer details (fromAccountId, toAccountId, amount, description)
     * @param userEmail email of the authenticated user (must own the source account)
     * @return TransactionResponse record of the transfer
     */
    TransactionResponse transfer(TransferRequest request, String userEmail);

    /**
     * Returns paginated transaction history for the specified account.
     *
     * @param accountId UUID of the account
     * @param userEmail email of the authenticated user (for ownership check)
     * @param page      zero-based page number
     * @param size      number of records per page
     * @param sortBy    field to sort by (default: "timestamp")
     * @param direction sort direction ("ASC" or "DESC")
     * @return PagedResponse wrapping a list of TransactionResponse objects
     */
    PagedResponse<TransactionResponse> getTransactionHistory(
            UUID accountId,
            String userEmail,
            int page,
            int size,
            String sortBy,
            String direction
    );
}
