package com.fastpay.presentation.mapper;

import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.model.Transaction;
import com.fastpay.presentation.controller.response.PixKeyOwnerResponse;
import com.fastpay.presentation.controller.response.PixKeyResponse;
import com.fastpay.presentation.controller.response.TransactionHistoryResponse;
import com.fastpay.presentation.controller.response.TransferResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PixWebMapper {
    PixKeyResponse toResponse(PixKey domain);

    @Mapping(source = "id", target = "transactionId")
    TransferResponse toResponse(Transaction domain);

    @Mapping(source = "type", target = "keyType")
    @Mapping(source = "value", target = "keyValue")
    @Mapping(source = "account.user.name", target = "ownerName")
    @Mapping(source = "account.user.document", target = "maskedDocument", qualifiedByName = "maskDocument")
    PixKeyOwnerResponse toOwnerResponse(PixKey domain);

    @Named("maskDocument")
    default String maskDocument(String document) {
        if (document == null || document.length() < 11) {
            return document;
        }
        return "***." + document.substring(3, 6) + "." + document.substring(6, 9) + "-**";
    }

    default TransactionHistoryResponse toHistoryResponse(Transaction transaction, @Context java.util.UUID accountId) {
        boolean isSender = transaction.getSender().getId().equals(accountId);

        String type = isSender ? "DEBIT" : "CREDIT";
        String counterpartyName = isSender ?
                transaction.getReceiver().getUser().getName() :
                transaction.getSender().getUser().getName();

        return new TransactionHistoryResponse(
                transaction.getId(),
                transaction.getAmountInCents(),
                transaction.getStatus(),
                transaction.getTimestamp(),
                type,
                counterpartyName
        );
    }
}