package com.fastpay.infra.persistence.postgres.repository;

import com.fastpay.infra.persistence.postgres.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmailOrDocument(String email, String document);
}