package com.fastpay.domain.port.in;

import com.fastpay.domain.model.Transaction;

import java.util.UUID;

public interface SendPixUseCase {
    Transaction send(UUID senderAccountId, String destinationKey, Long amountInCents);
}
