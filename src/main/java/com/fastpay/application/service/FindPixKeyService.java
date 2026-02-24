package com.fastpay.application.service;

import com.fastpay.domain.exception.KeyNotFoundException;
import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.port.in.FindPixKeyUseCase;
import com.fastpay.domain.port.out.PixKeyDatabasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindPixKeyService implements FindPixKeyUseCase {

    private final PixKeyDatabasePort pixKeyDatabasePort;

    @Override
    public PixKey findByValue(String value) {
        return pixKeyDatabasePort.findByValue(value)
                .orElseThrow(() -> new KeyNotFoundException("Pix key not found: " + value));
    }
}