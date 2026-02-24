package com.fastpay.application.service;

import com.fastpay.domain.model.Transaction;
import com.fastpay.domain.model.enums.TransactionStatus;
import com.fastpay.domain.port.in.ProcessSettlementUseCase;
import com.fastpay.domain.port.out.TransactionDatabasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessSettlementService implements ProcessSettlementUseCase {

    private final TransactionDatabasePort transactionDatabasePort;

    @Override
    @Transactional
    public void process(UUID transactionId, String currentStatus) {

        Transaction transaction = transactionDatabasePort.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found for ID: " + transactionId));

        if (transaction.getStatus() != TransactionStatus.PROCESSING) return;

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionDatabasePort.save(transaction);
    }
}