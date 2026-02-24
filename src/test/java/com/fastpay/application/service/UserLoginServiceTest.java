package com.fastpay.application.service;

import com.fastpay.domain.model.User;
import com.fastpay.domain.port.in.command.LoginCommand;
import com.fastpay.domain.port.out.PasswordEncoderPort;
import com.fastpay.domain.port.out.TokenGeneratorPort;
import com.fastpay.domain.port.out.UserDatabasePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {

    @Mock
    private UserDatabasePort userDatabasePort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private TokenGeneratorPort tokenGeneratorPort;

    @InjectMocks
    private UserLoginService userLoginService;

    private User user;
    private LoginCommand validCommand;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .document("12345678900")
                .email("john@example.com")
                .password("encoded_password_hash")
                .build();

        validCommand = new LoginCommand("john@example.com", "raw_password_123");
    }

    @Test
    @DisplayName("Should sign in successfully and return JWT token")
    void shouldSignInSuccessfully() {
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.payload.signature";

        when(userDatabasePort.findByEmail(validCommand.email())).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(validCommand.password(), user.getPassword())).thenReturn(true);
        when(tokenGeneratorPort.generateToken(user)).thenReturn(expectedToken);

        String token = userLoginService.signIn(validCommand);

        assertThat(token).isNotNull();
        assertThat(token).isEqualTo(expectedToken);

        verify(userDatabasePort).findByEmail(validCommand.email());
        verify(passwordEncoderPort).matches(validCommand.password(), user.getPassword());
        verify(tokenGeneratorPort).generateToken(user);
    }

    @Test
    @DisplayName("Should throw exception when email is not found")
    void shouldThrowExceptionWhenEmailNotFound() {
        LoginCommand invalidEmailCommand = new LoginCommand("wrong@example.com", "raw_password_123");

        when(userDatabasePort.findByEmail(invalidEmailCommand.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userLoginService.signIn(invalidEmailCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email");

        verify(passwordEncoderPort, never()).matches(any(), any());
        verify(tokenGeneratorPort, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should throw exception when password does not match")
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        LoginCommand wrongPasswordCommand = new LoginCommand("john@example.com", "wrong_password");

        when(userDatabasePort.findByEmail(wrongPasswordCommand.email())).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(wrongPasswordCommand.password(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userLoginService.signIn(wrongPasswordCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid password");

        verify(tokenGeneratorPort, never()).generateToken(any());
    }
}