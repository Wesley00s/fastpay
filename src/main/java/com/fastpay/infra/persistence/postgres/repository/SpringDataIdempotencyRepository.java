package com.fastpay.infra.persistence.postgres.repository;

import com.fastpay.infra.persistence.postgres.entity.IdempotencyRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataIdempotencyRepository extends JpaRepository<IdempotencyRecordEntity, UUID> {
    
    Optional<IdempotencyRecordEntity> findByIdempotencyKey(String idempotencyKey);
    
}