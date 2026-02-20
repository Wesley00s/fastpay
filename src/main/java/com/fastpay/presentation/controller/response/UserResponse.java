package com.fastpay.presentation.controller.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String document,
        String email
) {
}