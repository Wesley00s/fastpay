package com.fastpay.infra.persistence.postgres.adapter;

import com.fastpay.domain.model.Account;
import com.fastpay.domain.port.out.AccountDatabasePort;
import com.fastpay.infra.persistence.postgres.mapper.AccountDatabaseMapper;
import com.fastpay.infra.persistence.postgres.repository.SpringDataAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountPostgresAdapter implements AccountDatabasePort {

    private final SpringDataAccountRepository repository;
    private final AccountDatabaseMapper mapper;

    @Override
    public Optional<Account> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Account update(Account account) {
        return mapper.toDomain(repository.save(mapper.toEntity(account)));
    }

    @Override
    public Account save(Account account) {
        return mapper.toDomain(repository.save(mapper.toEntity(account)));
    }

    @Override
    public Optional<Account> findByUserEmail(String email) {
        return repository.findByUserEmail(email).map(mapper::toDomain);
    }
}