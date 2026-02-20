package com.fastpay.domain.port.out;

import com.fastpay.domain.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountDatabasePort {
    Optional<Account> findById(UUID id);

    Account update(Account account);

    Account save(Account account);
}