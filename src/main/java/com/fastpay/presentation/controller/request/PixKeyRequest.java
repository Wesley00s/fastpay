package com.fastpay.presentation.controller.request;

import jakarta.validation.constraints.NotBlank;

public record PixKeyRequest(
        @NotBlank(message = "Key type is mandatory")
        String type,
        @NotBlank(message = "Key value is mandatory")
        String value
) {
}