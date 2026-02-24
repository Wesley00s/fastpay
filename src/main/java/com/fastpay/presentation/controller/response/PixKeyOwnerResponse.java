package com.fastpay.presentation.controller.response;

public record PixKeyOwnerResponse(
        String keyType,
        String keyValue,
        String ownerName,
        String maskedDocument
) {
}