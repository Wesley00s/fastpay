package com.fastpay.presentation.controller.response;

import java.util.UUID;

public record AccountDetailsResponse(
        UUID id,
        String agency,
        String number,
        Long balanceInCents,
        String ownerName
) {
}