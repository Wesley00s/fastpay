package com.fastpay.domain.port.out;

import com.fastpay.domain.model.Transaction;

public interface TransactionDatabasePort {
    Transaction save(Transaction transaction);
}