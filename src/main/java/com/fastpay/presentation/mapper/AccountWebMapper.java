package com.fastpay.presentation.mapper;

import com.fastpay.domain.model.Account;
import com.fastpay.presentation.controller.response.AccountDetailsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountWebMapper {

    @Mapping(source = "user.name", target = "ownerName")
    AccountDetailsResponse toResponse(Account domain);
}