package com.fastpay.domain.port.out;

import com.fastpay.domain.model.PixKey;

import java.util.Optional;

public interface PixKeyDatabasePort {
    PixKey save(PixKey pixKey);

    boolean existsByValue(String value);

    Optional<PixKey> findByValue(String value);
}
