package com.fastpay.infra.persistence.postgres.mapper;

import com.fastpay.domain.model.Account;
import com.fastpay.infra.persistence.postgres.entity.AccountEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountDatabaseMapper {
    AccountEntity toEntity(Account domain);

    Account toDomain(AccountEntity entity);
}