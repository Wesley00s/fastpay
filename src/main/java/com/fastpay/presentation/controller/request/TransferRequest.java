package com.fastpay.presentation.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferRequest(
        @NotBlank(message = "Destination key is mandatory")
        String destinationKey,

        @NotNull(message = "Amount is mandatory")
        @Positive(message = "Amount must be greater than zero")
        Long amountInCents
) {
}