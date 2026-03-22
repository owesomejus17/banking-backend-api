package com.securebank.dto.request;

import com.securebank.entity.Account;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for POST /api/v1/accounts
 */
public class CreateAccountRequest {

    @NotNull(message = "Account type is required (SAVINGS or CURRENT)")
    private Account.AccountType accountType;

    public CreateAccountRequest() {
    }

    public Account.AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(Account.AccountType accountType) {
        this.accountType = accountType;
    }
}
