package com.fastpay.infra.persistence.postgres.repository;

import com.fastpay.infra.persistence.postgres.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAccountRepository extends JpaRepository<AccountEntity, UUID> {

    @Query("SELECT a FROM AccountEntity a JOIN FETCH a.user u WHERE u.email = :email")
    Optional<AccountEntity> findByUserEmail(@Param("email") String email);
}