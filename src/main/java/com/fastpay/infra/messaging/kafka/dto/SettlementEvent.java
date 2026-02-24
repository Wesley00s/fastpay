package com.fastpay.infra.messaging.kafka.dto;

import java.util.UUID;

public record SettlementEvent(
        UUID transactionId,
        String status
) {
}