package com.fastpay.application.service;

import com.fastpay.domain.exception.UserAlreadyExistsException;
import com.fastpay.domain.model.Account;
import com.fastpay.domain.model.User;
import com.fastpay.domain.port.in.command.SignUpCommand;
import com.fastpay.domain.port.out.AccountDatabasePort;
import com.fastpay.domain.port.out.PasswordEncoderPort;
import com.fastpay.domain.port.out.UserDatabasePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSignUpServiceTest {

    @Mock
    private UserDatabasePort userDatabasePort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private AccountDatabasePort accountDatabasePort;

    @InjectMocks
    private UserSignUpService userSignUpService;

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    private SignUpCommand signUpCommand;
    private User savedUser;

    @BeforeEach
    void setUp() {
        signUpCommand = new SignUpCommand(
                "Jane Doe",
                "09876543211",
                "jane@example.com",
                "raw_password_123"
        );

        savedUser = User.builder()
                .id(UUID.randomUUID())
                .name(signUpCommand.name())
                .document(signUpCommand.document())
                .email(signUpCommand.email())
                .password("encoded_password_hash")
                .build();
    }

    @Test
    @DisplayName("Should sign up user successfully and provision a new bank account")
    void shouldSignUpSuccessfully() {
        when(userDatabasePort.existsByEmailOrDocument(signUpCommand.email(), signUpCommand.document())).thenReturn(false);
        when(passwordEncoderPort.encode(signUpCommand.password())).thenReturn("encoded_password_hash");
        when(userDatabasePort.save(any(User.class))).thenReturn(savedUser);

        User result = userSignUpService.signUp(signUpCommand);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedUser.getId());
        assertThat(result.getPassword()).isEqualTo("encoded_password_hash");

        verify(userDatabasePort).existsByEmailOrDocument(signUpCommand.email(), signUpCommand.document());
        verify(passwordEncoderPort).encode(signUpCommand.password());
        verify(userDatabasePort).save(any(User.class));

        verify(accountDatabasePort).save(accountCaptor.capture());
        Account provisionedAccount = accountCaptor.getValue();

        assertThat(provisionedAccount.getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(provisionedAccount.getAgency()).isEqualTo("0001");
        assertThat(provisionedAccount.getBalanceInCents()).isZero();
        assertThat(provisionedAccount.getNumber()).matches("\\d{6}-\\d");
    }

    @Test
    @DisplayName("Should throw exception when email or document already exists")
    void shouldThrowExceptionWhenUserAlreadyExists() {
        when(userDatabasePort.existsByEmailOrDocument(signUpCommand.email(), signUpCommand.document())).thenReturn(true);

        assertThatThrownBy(() -> userSignUpService.signUp(signUpCommand))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("User already exists");

        verify(passwordEncoderPort, never()).encode(anyString());
        verify(userDatabasePort, never()).save(any());
        verify(accountDatabasePort, never()).save(any());
    }
}