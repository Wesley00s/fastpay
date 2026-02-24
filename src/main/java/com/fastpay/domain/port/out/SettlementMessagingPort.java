package com.fastpay.domain.port.out;

import com.fastpay.domain.model.Transaction;

public interface SettlementMessagingPort {
    void sendSettlementEvent(Transaction transaction);
}