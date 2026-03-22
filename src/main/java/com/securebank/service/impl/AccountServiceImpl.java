package com.securebank.service.impl;

import com.securebank.dto.request.CreateAccountRequest;
import com.securebank.dto.response.AccountResponse;
import com.securebank.entity.Account;
import com.securebank.entity.User;
import com.securebank.exception.ResourceNotFoundException;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.UserRepository;
import com.securebank.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Account account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);

        Account saved = accountRepository.save(account);
        log.info("Account created: {} ({}) for user: {}", saved.getAccountNumber(), saved.getAccountType(), userEmail);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getMyAccounts(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        return accountRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID accountId, String userEmail) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));

        if (!account.getUser().getEmail().equals(userEmail)) {
            log.warn("Ownership violation: user {} tried to access account {}", userEmail, accountId);
            throw new AccessDeniedException("You do not have access to this account");
        }

        return account.getBalance();
    }

    // ─── Mapping ────────────────────────────────────────────────────────────────

    private AccountResponse toResponse(Account account) {
        AccountResponse r = new AccountResponse();
        r.setId(account.getId());
        r.setAccountNumber(account.getAccountNumber());
        r.setAccountType(account.getAccountType());
        r.setBalance(account.getBalance());
        r.setCreatedAt(account.getCreatedAt());
        return r;
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private String generateUniqueAccountNumber() {
        Random random = new Random();
        String accountNumber;
        int attempts = 0;
        do {
            long number = 1_000_000_000L + (long) (random.nextDouble() * 9_000_000_000L);
            accountNumber = String.valueOf(number);
            if (++attempts > 10) {
                throw new RuntimeException("Unable to generate unique account number after 10 attempts");
            }
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
