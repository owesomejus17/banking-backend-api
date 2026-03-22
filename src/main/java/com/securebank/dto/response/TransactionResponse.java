package com.securebank.dto.response;

import com.securebank.entity.Transaction;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Safe representation of a Transaction returned in API responses.
 */
public class TransactionResponse {

    private UUID id;
    // Null for deposits (no source account)
    private UUID fromAccountId;
    // Null for withdrawals (no destination account)
    private UUID toAccountId;
    private BigDecimal amount;
    private Transaction.TransactionType type;
    private String description;
    private LocalDateTime timestamp;
    private Transaction.TransactionStatus status;

    public TransactionResponse() {}

    public TransactionResponse(UUID id, UUID fromAccountId, UUID toAccountId, BigDecimal amount,
            Transaction.TransactionType type, String description, LocalDateTime timestamp,
            Transaction.TransactionStatus status) {
        this.id = id;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.timestamp = timestamp;
        this.status = status;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(UUID fromAccountId) { this.fromAccountId = fromAccountId; }

    public UUID getToAccountId() { return toAccountId; }
    public void setToAccountId(UUID toAccountId) { this.toAccountId = toAccountId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Transaction.TransactionType getType() { return type; }
    public void setType(Transaction.TransactionType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Transaction.TransactionStatus getStatus() { return status; }
    public void setStatus(Transaction.TransactionStatus status) { this.status = status; }
}
