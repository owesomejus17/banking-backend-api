package com.securebank.repository;

import com.securebank.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Data access layer for {@link Transaction} entities.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    /**
     * Retrieves paginated transaction history for a given account.
     */
    Page<Transaction> findByFromAccountIdOrToAccountId(
            UUID fromAccountId,
            UUID toAccountId,
            Pageable pageable
    );
}
