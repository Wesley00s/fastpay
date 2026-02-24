package com.fastpay.application.service;

import com.fastpay.domain.model.Transaction;
import com.fastpay.domain.pagination.PageResult;
import com.fastpay.domain.port.in.GetTransactionHistoryUseCase;
import com.fastpay.domain.port.out.AccountDatabasePort;
import com.fastpay.domain.port.out.TransactionDatabasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetTransactionHistoryService implements GetTransactionHistoryUseCase {

    private final TransactionDatabasePort transactionDatabasePort;
    private final AccountDatabasePort accountDatabasePort;

    @Override
    public PageResult<Transaction> getHistory(UUID accountId, int page, int size) {

        accountDatabasePort.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for ID: " + accountId));

        return transactionDatabasePort.findHistoryByAccountId(accountId, page, size);
    }
}