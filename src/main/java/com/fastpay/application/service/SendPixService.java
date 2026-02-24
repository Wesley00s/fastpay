package com.fastpay.application.service;

import com.fastpay.domain.exception.InsufficientBalanceException;
import com.fastpay.domain.exception.KeyNotFoundException;
import com.fastpay.domain.model.Account;
import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.model.Transaction;
import com.fastpay.domain.model.enums.TransactionStatus;
import com.fastpay.domain.port.in.SendPixUseCase;
import com.fastpay.domain.port.out.AccountDatabasePort;
import com.fastpay.domain.port.out.PixKeyDatabasePort;
import com.fastpay.domain.port.out.SettlementMessagingPort;
import com.fastpay.domain.port.out.TransactionDatabasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SendPixService implements SendPixUseCase {

    private final AccountDatabasePort accountDatabasePort;
    private final PixKeyDatabasePort pixKeyDatabasePort;
    private final TransactionDatabasePort transactionDatabasePort;
    private final SettlementMessagingPort settlementMessagingPort;

    @Override
    @Transactional
    public Transaction send(UUID senderAccountId, String destinationKey, Long amountInCents) {

        Account senderAccount = accountDatabasePort.findById(senderAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Sender account not found"));

        PixKey destinationPixKey = pixKeyDatabasePort.findByValue(destinationKey)
                .orElseThrow(() -> new KeyNotFoundException("Destination Pix key not found: " + destinationKey));

        Account receiverAccount = destinationPixKey.getAccount();

        if (senderAccount.getId().equals(receiverAccount.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        try {
            senderAccount.debit(amountInCents);
        } catch (IllegalStateException e) {
            throw new InsufficientBalanceException("Insufficient balance to complete the transfer");
        }

        receiverAccount.credit(amountInCents);

        accountDatabasePort.update(senderAccount);
        accountDatabasePort.update(receiverAccount);

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .sender(senderAccount)
                .receiver(receiverAccount)
                .amountInCents(amountInCents)
                .status(TransactionStatus.PROCESSING)
                .timestamp(Instant.now())
                .build();

        Transaction savedTransaction = transactionDatabasePort.save(transaction);

        settlementMessagingPort.sendSettlementEvent(savedTransaction);
        return savedTransaction;
    }
}