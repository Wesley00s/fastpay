package com.fastpay.infra.persistence.postgres.adapter;

import com.fastpay.domain.model.Transaction;
import com.fastpay.domain.pagination.PageResult;
import com.fastpay.domain.port.out.TransactionDatabasePort;
import com.fastpay.infra.persistence.postgres.entity.TransactionEntity;
import com.fastpay.infra.persistence.postgres.mapper.TransactionDatabaseMapper;
import com.fastpay.infra.persistence.postgres.repository.SpringDataTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionPostgresAdapter implements TransactionDatabasePort {

    private final SpringDataTransactionRepository repository;
    private final TransactionDatabaseMapper mapper;

    @Override
    public Transaction save(Transaction transaction) {
        return mapper.toDomain(repository.save(mapper.toEntity(transaction)));
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }


    @Override
    public PageResult<Transaction> findHistoryByAccountId(UUID accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionEntity> entityPage = repository.findHistoryByAccountId(accountId, pageable);

        java.util.List<Transaction> domainList = entityPage.getContent().stream()
                .map(mapper::toDomain)
                .toList();

        return new PageResult<>(
                domainList,
                entityPage.getNumber(),
                entityPage.getTotalPages(),
                entityPage.getTotalElements()
        );
    }
}