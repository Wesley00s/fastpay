package com.fastpay.presentation.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DepositRequest(
        @NotNull(message = "Deposit amount is mandatory")
        @Positive(message = "Deposit amount must be greater than zero")
        Long amountInCents
) {
}