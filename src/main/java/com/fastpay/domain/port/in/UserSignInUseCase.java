package com.fastpay.domain.port.in;

import com.fastpay.domain.port.in.command.LoginCommand;

public interface UserSignInUseCase {
    String signIn(LoginCommand command);
}
