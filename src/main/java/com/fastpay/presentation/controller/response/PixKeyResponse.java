package com.fastpay.presentation.controller.response;

import com.fastpay.domain.model.enums.KeyType;

import java.time.Instant;
import java.util.UUID;

public record PixKeyResponse(
        UUID id,
        KeyType type,
        String value,
        Instant createdAt
) {
}