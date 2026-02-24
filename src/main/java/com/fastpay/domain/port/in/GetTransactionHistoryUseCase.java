package com.fastpay.domain.port.in;

import com.fastpay.domain.model.Transaction;
import com.fastpay.domain.pagination.PageResult;

import java.util.UUID;

public interface GetTransactionHistoryUseCase {
    PageResult<Transaction> getHistory(UUID accountId, int page, int size);
}