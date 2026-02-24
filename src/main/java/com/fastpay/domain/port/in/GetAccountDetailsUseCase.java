package com.fastpay.domain.port.in;

import com.fastpay.domain.model.Account;

public interface GetAccountDetailsUseCase {
    Account getDetailsByEmail(String email);
}