package com.fastpay.domain.port.out;

import com.fastpay.domain.model.User;

public interface TokenGeneratorPort {
    String generateToken(User user);
}