package com.fastpay.domain.port.out;

import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.pagination.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface PixKeyDatabasePort {
    PixKey save(PixKey pixKey);

    boolean existsByValue(String value);

    Optional<PixKey> findByValue(String value);

    PageResult<PixKey> findByAccountId(UUID accountId, int page, int size);
}