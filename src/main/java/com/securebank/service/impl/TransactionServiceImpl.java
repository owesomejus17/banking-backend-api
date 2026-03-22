package com.securebank.service.impl;

import com.securebank.dto.request.DepositRequest;
import com.securebank.dto.request.TransferRequest;
import com.securebank.dto.request.WithdrawRequest;
import com.securebank.dto.response.PagedResponse;
import com.securebank.dto.response.TransactionResponse;
import com.securebank.entity.Account;
import com.securebank.entity.Transaction;
import com.securebank.exception.InsufficientFundsException;
import com.securebank.exception.ResourceNotFoundException;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.TransactionRepository;
import com.securebank.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(AccountRepository accountRepository,
                                   TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public TransactionResponse deposit(DepositRequest request, String userEmail) {
        Account account = getOwnedAccount(request.getAccountId(), userEmail);
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);
        log.info("Deposit: {} credited {} to account {}", userEmail, request.getAmount(), account.getAccountNumber());

        Transaction tx = buildTx(null, account, request.getAmount(),
                Transaction.TransactionType.DEPOSIT, request.getDescription(),
                Transaction.TransactionStatus.SUCCESS);
        return toResponse(transactionRepository.save(tx));
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request, String userEmail) {
        Account account = getOwnedAccount(request.getAccountId(), userEmail);

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            transactionRepository.save(buildTx(null, account, request.getAmount(),
                    Transaction.TransactionType.WITHDRAWAL, request.getDescription(),
                    Transaction.TransactionStatus.FAILED));
            log.warn("Withdrawal FAILED: insufficient funds in account {}", account.getAccountNumber());
            throw new InsufficientFundsException(
                    "Insufficient funds. Available: " + account.getBalance() + ", Requested: " + request.getAmount());
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);
        log.info("Withdrawal: {} debited {} from account {}", userEmail, request.getAmount(), account.getAccountNumber());

        Transaction tx = buildTx(account, null, request.getAmount(),
                Transaction.TransactionType.WITHDRAWAL, request.getDescription(),
                Transaction.TransactionStatus.SUCCESS);
        return toResponse(transactionRepository.save(tx));
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request, String userEmail) {
        Account fromAccount = getOwnedAccount(request.getFromAccountId(), userEmail);
        Account toAccount = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getToAccountId()));

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            transactionRepository.save(buildTx(fromAccount, toAccount, request.getAmount(),
                    Transaction.TransactionType.TRANSFER, request.getDescription(),
                    Transaction.TransactionStatus.FAILED));
            throw new InsufficientFundsException(
                    "Insufficient funds. Available: " + fromAccount.getBalance() + ", Requested: " + request.getAmount());
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        log.info("Transfer: {} moved {} from {} to {}", userEmail, request.getAmount(),
                fromAccount.getAccountNumber(), toAccount.getAccountNumber());

        Transaction tx = buildTx(fromAccount, toAccount, request.getAmount(),
                Transaction.TransactionType.TRANSFER, request.getDescription(),
                Transaction.TransactionStatus.SUCCESS);
        return toResponse(transactionRepository.save(tx));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TransactionResponse> getTransactionHistory(
            UUID accountId, String userEmail, int page, int size, String sortBy, String direction) {

        getOwnedAccount(accountId, userEmail);

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Transaction> txPage = transactionRepository
                .findByFromAccountIdOrToAccountId(accountId, accountId, pageable);

        List<TransactionResponse> content = txPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PagedResponse.<TransactionResponse>builder()
                .content(content)
                .pageNumber(txPage.getNumber())
                .pageSize(txPage.getSize())
                .totalElements(txPage.getTotalElements())
                .totalPages(txPage.getTotalPages())
                .last(txPage.isLast())
                .build();
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private Account getOwnedAccount(UUID accountId, String userEmail) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        if (!account.getUser().getEmail().equals(userEmail)) {
            log.warn("Ownership violation: user {} attempted operation on account {}", userEmail, accountId);
            throw new AccessDeniedException("You do not have access to account: " + accountId);
        }
        return account;
    }

    private Transaction buildTx(Account from, Account to, BigDecimal amount,
                                 Transaction.TransactionType type, String description,
                                 Transaction.TransactionStatus status) {
        Transaction tx = new Transaction();
        tx.setFromAccount(from);
        tx.setToAccount(to);
        tx.setAmount(amount);
        tx.setType(type);
        tx.setDescription(description);
        tx.setTimestamp(LocalDateTime.now());
        tx.setStatus(status);
        return tx;
    }

    private TransactionResponse toResponse(Transaction tx) {
        TransactionResponse r = new TransactionResponse();
        r.setId(tx.getId());
        r.setFromAccountId(tx.getFromAccount() != null ? tx.getFromAccount().getId() : null);
        r.setToAccountId(tx.getToAccount() != null ? tx.getToAccount().getId() : null);
        r.setAmount(tx.getAmount());
        r.setType(tx.getType());
        r.setDescription(tx.getDescription());
        r.setTimestamp(tx.getTimestamp());
        r.setStatus(tx.getStatus());
        return r;
    }
}
