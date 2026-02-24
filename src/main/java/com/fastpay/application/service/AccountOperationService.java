package com.fastpay.application.service;

import com.fastpay.domain.model.Account;
import com.fastpay.domain.port.in.DepositUseCase;
import com.fastpay.domain.port.in.GetAccountDetailsUseCase;
import com.fastpay.domain.port.out.AccountDatabasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountOperationService implements GetAccountDetailsUseCase, DepositUseCase {

    private final AccountDatabasePort accountDatabasePort;

    @Override
    public Account getDetails(UUID accountId) {
        return accountDatabasePort.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for ID: " + accountId));
    }

    @Override
    @Transactional
    public Account deposit(UUID accountId, Long amountInCents) {

        Account account = accountDatabasePort.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for ID: " + accountId));

        account.credit(amountInCents);

        return accountDatabasePort.update(account);
    }
}