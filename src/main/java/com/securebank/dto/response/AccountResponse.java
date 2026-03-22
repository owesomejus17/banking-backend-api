package com.securebank.dto.response;

import com.securebank.entity.Account;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Safe representation of an Account returned in API responses.
 * Never exposes internal entity fields directly.
 */
public class AccountResponse {

    private UUID id;
    private String accountNumber;
    private Account.AccountType accountType;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public AccountResponse() {
    }

    public AccountResponse(UUID id, String accountNumber, Account.AccountType accountType, BigDecimal balance, LocalDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Account.AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(Account.AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
