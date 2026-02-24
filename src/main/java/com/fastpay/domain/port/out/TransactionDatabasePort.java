package com.fastpay.domain.port.out;

import com.fastpay.domain.model.Transaction;
import java.util.Optional;
import java.util.UUID;

public interface TransactionDatabasePort {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(UUID id);
}