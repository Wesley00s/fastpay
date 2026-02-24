package com.fastpay.infra.persistence.postgres.repository;

import com.fastpay.infra.persistence.postgres.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SpringDataTransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("SELECT t FROM TransactionEntity t WHERE t.sender.id = :accountId OR t.receiver.id = :accountId ORDER BY t.timestamp DESC")
    Page<TransactionEntity> findHistoryByAccountId(@Param("accountId") UUID accountId, Pageable pageable);
}
