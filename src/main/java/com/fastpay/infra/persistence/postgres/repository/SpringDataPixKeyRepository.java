package com.fastpay.infra.persistence.postgres.repository;

import com.fastpay.infra.persistence.postgres.entity.PixKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataPixKeyRepository extends JpaRepository<PixKeyEntity, UUID> {
    boolean existsByValue(String value);

    Optional<PixKeyEntity> findByValue(String value);
}