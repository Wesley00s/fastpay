package com.fastpay.infra.persistence.postgres.adapter;

import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.port.out.PixKeyDatabasePort;
import com.fastpay.infra.persistence.postgres.entity.PixKeyEntity;
import com.fastpay.infra.persistence.postgres.mapper.PixKeyDatabaseMapper;
import com.fastpay.infra.persistence.postgres.repository.SpringDataPixKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;


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
}