package com.fastpay.domain.port.in;

import java.util.UUID;

public interface ProcessSettlementUseCase {
    void process(UUID transactionId, String currentStatus);
}