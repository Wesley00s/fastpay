package com.fastpay.presentation.controller.request;

import com.fastpay.domain.model.enums.KeyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PixKeyRequest(

        @NotNull(message = "Account ID is mandatory")
        UUID accountId,

        @NotNull(message = "Key type is mandatory")
        KeyType type,

        @NotBlank(message = "Key value is mandatory")
        String value
) {
}