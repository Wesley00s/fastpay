package com.fastpay.domain.port.in;

import com.fastpay.domain.model.User;
import com.fastpay.domain.port.in.command.SignUpCommand;

public interface UserSignUpUseCase {
    User signUp(SignUpCommand command);
}