package com.fastpay.domain.port.in;

import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.model.enums.KeyType;

import java.util.UUID;

public interface RegisterPixKeyUseCase {
    PixKey register(UUID accountId, KeyType type, String value);

}
