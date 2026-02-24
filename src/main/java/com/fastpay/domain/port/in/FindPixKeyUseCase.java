package com.fastpay.domain.port.in;

import com.fastpay.domain.model.PixKey;

public interface FindPixKeyUseCase {
    PixKey findByValue(String value);
}