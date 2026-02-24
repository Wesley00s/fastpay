package com.fastpay.domain.port.in;

import com.fastpay.domain.model.Account;

import java.util.UUID;

public interface GetAccountDetailsUseCase {
    Account getDetails(UUID accountId);
}