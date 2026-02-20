package com.fastpay.application.service;

import com.fastpay.domain.model.User;
import com.fastpay.domain.port.in.UserSignInUseCase;
import com.fastpay.domain.port.in.command.LoginCommand;
import com.fastpay.domain.port.out.PasswordEncoderPort;
import com.fastpay.domain.port.out.TokenGeneratorPort;
import com.fastpay.domain.port.out.UserDatabasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginService implements UserSignInUseCase {

    private final UserDatabasePort userDatabasePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenGeneratorPort tokenGeneratorPort;

    @Override
    public String signIn(LoginCommand command) {

        User user = userDatabasePort.findByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email"));

        if (!passwordEncoderPort.matches(command.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return tokenGeneratorPort.generateToken(user);
    }
}