package com.fastpay.presentation.controller.response;

public record TokenResponse(
        String token,
        String type
) {
}