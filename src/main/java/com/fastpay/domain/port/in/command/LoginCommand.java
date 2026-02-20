package com.fastpay.domain.port.in.command;

public record LoginCommand(
        String email,
        String password
) {
}