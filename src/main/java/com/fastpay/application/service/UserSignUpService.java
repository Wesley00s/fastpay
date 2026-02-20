package com.fastpay.application.service;

import com.fastpay.domain.exception.UserAlreadyExistsException;
import com.fastpay.domain.model.Account;
import com.fastpay.domain.model.User;
import com.fastpay.domain.port.in.UserSignUpUseCase;
import com.fastpay.domain.port.in.command.SignUpCommand;
import com.fastpay.domain.port.out.AccountDatabasePort;
import com.fastpay.domain.port.out.PasswordEncoderPort;
import com.fastpay.domain.port.out.UserDatabasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSignUpService implements UserSignUpUseCase {

    private final UserDatabasePort userDatabasePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final AccountDatabasePort accountDatabasePort;

    @Override
    @Transactional
    public User signUp(SignUpCommand command) {

        if (userDatabasePort.existsByEmailOrDocument(command.email(), command.document())) {
            throw new UserAlreadyExistsException("User already exists with the provided email or document.");
        }

        String encodedPassword = passwordEncoderPort.encode(command.password());

        User newUser = User.builder()
                .id(UUID.randomUUID())
                .name(command.name())
                .document(command.document())
                .email(command.email())
                .password(encodedPassword)
                .build();

        User savedUser = userDatabasePort.save(newUser);

        Account newAccount = Account.builder()
                .id(UUID.randomUUID())
                .user(savedUser)
                .agency("0001")
                .number(generateAccountNumber())
                .balanceInCents(0L)
                .build();

        accountDatabasePort.save(newAccount);
        return savedUser;
    }

    private String generateAccountNumber() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        int digit = random.nextInt(10);
        return number + "-" + digit;
    }
}