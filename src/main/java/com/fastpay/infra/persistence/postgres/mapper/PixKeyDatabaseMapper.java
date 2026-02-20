package com.fastpay.infra.persistence.postgres.mapper;

import com.fastpay.domain.model.PixKey;
import com.fastpay.infra.persistence.postgres.entity.PixKeyEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AccountDatabaseMapper.class})
public interface PixKeyDatabaseMapper {
    PixKeyEntity toEntity(PixKey domain);

    PixKey toDomain(PixKeyEntity entity);
}