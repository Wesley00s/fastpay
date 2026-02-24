package com.fastpay.domain.port.in;

import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.pagination.PageResult;

import java.util.UUID;

public interface ListPixKeysUseCase {
    PageResult<PixKey> listByAccountId(UUID accountId, int page, int size);
}