package com.fastpay.presentation.mapper;

import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.model.Transaction;
import com.fastpay.presentation.controller.response.PixKeyResponse;
import com.fastpay.presentation.controller.response.TransferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PixWebMapper {
    PixKeyResponse toResponse(PixKey domain);

    @Mapping(source = "id", target = "transactionId")
    TransferResponse toResponse(Transaction domain);

}