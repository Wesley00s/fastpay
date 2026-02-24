package com.fastpay.application.service;

import com.fastpay.domain.model.Transaction;
import com.fastpay.domain.model.enums.TransactionStatus;
import com.fastpay.domain.port.in.ProcessSettlementUseCase;
import com.fastpay.domain.port.out.TransactionDatabasePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessSettlementService implements ProcessSettlementUseCase {

    private final TransactionDatabasePort transactionDatabasePort;

    @Override
    @Transactional
    public void process(UUID transactionId, String currentStatus) {
        log.info("Starting settlement process for transaction ID: {}", transactionId);

        Transaction transaction = transactionDatabasePort.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found for ID: " + transactionId));

        if (transaction.getStatus() != TransactionStatus.PROCESSING) {
            log.warn("Transaction {} is not in PROCESSING state. Current state: {}", transactionId, transaction.getStatus());
            return;
        }

        try {
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Settlement process interrupted for transaction {}", transactionId);
        }

        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionDatabasePort.save(transaction);
        
        log.info("Transaction {} successfully settled and marked as COMPLETED", transactionId);
    }
}