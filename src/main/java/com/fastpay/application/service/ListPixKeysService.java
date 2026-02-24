package com.fastpay.application.service;

import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.pagination.PageResult;
import com.fastpay.domain.port.in.ListPixKeysUseCase;
import com.fastpay.domain.port.out.PixKeyDatabasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListPixKeysService implements ListPixKeysUseCase {

    private final PixKeyDatabasePort pixKeyDatabasePort;

    @Override
    public PageResult<PixKey> listByAccountId(UUID accountId, int page, int size) {
        return pixKeyDatabasePort.findByAccountId(accountId, page, size);
    }
}