package com.fastpay.presentation.controller.response;

import com.fastpay.domain.model.enums.TransactionStatus;

import java.time.Instant;
import java.util.UUID;

public record TransferResponse(
        UUID transactionId,
        TransactionStatus status,
        Instant timestamp
) {
}