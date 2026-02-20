package com.fastpay.infra.persistence.postgres.mapper;

import com.fastpay.domain.model.User;
import com.fastpay.infra.persistence.postgres.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDatabaseMapper {
    UserEntity toEntity(User domain);

    User toDomain(UserEntity entity);
}