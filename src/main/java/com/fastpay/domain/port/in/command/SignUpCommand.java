package com.fastpay.domain.port.in.command;

public record SignUpCommand(
        String name,
        String document,
        String email,
        String password
) {
}