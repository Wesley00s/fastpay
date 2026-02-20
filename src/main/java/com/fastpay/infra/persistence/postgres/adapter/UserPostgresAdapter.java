package com.fastpay.infra.persistence.postgres.adapter;

import com.fastpay.domain.model.User;
import com.fastpay.domain.port.out.UserDatabasePort;
import com.fastpay.infra.persistence.postgres.mapper.UserDatabaseMapper;
import com.fastpay.infra.persistence.postgres.repository.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPostgresAdapter implements UserDatabasePort {

    private final SpringDataUserRepository repository;
    private final UserDatabaseMapper mapper;

    @Override
    public User save(User user) {
        return mapper.toDomain(repository.save(mapper.toEntity(user)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmailOrDocument(String email, String document) {
        return repository.existsByEmailOrDocument(email, document);
    }
}