package com.fastpay.application.service;


import com.fastpay.domain.model.Account;
import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.model.enums.KeyType;
import com.fastpay.domain.port.in.RegisterPixKeyUseCase;
import com.fastpay.domain.port.out.AccountDatabasePort;
import com.fastpay.domain.port.out.PixKeyDatabasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterPixKeyService implements RegisterPixKeyUseCase {

    private final PixKeyDatabasePort pixKeyDatabasePort;
    private final AccountDatabasePort accountDatabasePort;

    @Override
    public PixKey register(UUID accountId, KeyType type, String value) {
        Account account = accountDatabasePort.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for ID: " + accountId));

        if (pixKeyDatabasePort.existsByValue(value)) {
            throw new IllegalArgumentException("Pix key already exists: " + value);
        }

        PixKey pixKey = PixKey.builder()
                .id(UUID.randomUUID())
                .account(account)
                .type(type)
                .value(value)
                .createdAt(Instant.now())
                .build();

        return pixKeyDatabasePort.save(pixKey);
    }
}
