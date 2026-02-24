package com.fastpay.infra.persistence.postgres.adapter;

import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.pagination.PageResult;
import com.fastpay.domain.port.out.PixKeyDatabasePort;
import com.fastpay.infra.persistence.postgres.entity.PixKeyEntity;
import com.fastpay.infra.persistence.postgres.mapper.PixKeyDatabaseMapper;
import com.fastpay.infra.persistence.postgres.repository.SpringDataPixKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class PixKeyPostgresAdapter implements PixKeyDatabasePort {

    private final SpringDataPixKeyRepository repository;
    private final PixKeyDatabaseMapper mapper;

    @Override
    public PixKey save(PixKey pixKey) {
        PixKeyEntity entity = mapper.toEntity(pixKey);
        PixKeyEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByValue(String value) {
        return repository.existsByValue(value);
    }

    @Override
    public Optional<PixKey> findByValue(String value) {
        return repository.findByValue(value).map(mapper::toDomain);
    }

    @Override
    public PageResult<PixKey> findByAccountId(UUID accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PixKeyEntity> entityPage = repository.findByAccountId(accountId, pageable);

        List<PixKey> domainKeys = entityPage.getContent().stream()
                .map(mapper::toDomain)
                .toList();

        return new PageResult<>(
                domainKeys,
                entityPage.getNumber(),
                entityPage.getTotalPages(),
                entityPage.getTotalElements()
        );
    }
}