package com.fastpay.infra.persistence.postgres.mapper;

import com.fastpay.domain.model.Transaction;
import com.fastpay.infra.persistence.postgres.entity.TransactionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AccountDatabaseMapper.class})
public interface TransactionDatabaseMapper {
    TransactionEntity toEntity(Transaction domain);

    Transaction toDomain(TransactionEntity entity);
}