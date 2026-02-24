package com.fastpay.presentation.controller.response;

import com.fastpay.domain.model.enums.TransactionStatus;

import java.time.Instant;
import java.util.UUID;

public record TransactionHistoryResponse(
        UUID transactionId,
        Long amountInCents,
        TransactionStatus status,
        Instant timestamp,
        String type,
        String counterpartyName
) {
}