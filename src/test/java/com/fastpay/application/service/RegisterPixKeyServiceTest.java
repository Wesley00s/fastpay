package com.fastpay.application.service;

import com.fastpay.domain.model.Account;
import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.model.User;
import com.fastpay.domain.model.enums.KeyType;
import com.fastpay.domain.port.out.AccountDatabasePort;
import com.fastpay.domain.port.out.PixKeyDatabasePort;
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
class RegisterPixKeyServiceTest {

    @Mock
    private PixKeyDatabasePort pixKeyDatabasePort;

    @Mock
    private AccountDatabasePort accountDatabasePort;

    @InjectMocks
    private RegisterPixKeyService registerPixKeyService;

    private Account account;
    private UUID accountId;
    private String keyValue;
    private KeyType keyType;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        keyValue = "user@example.com";
        keyType = KeyType.EMAIL;

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("user@example.com")
                .build();

        account = Account.builder()
                .id(accountId)
                .agency("0001")
                .number("12345-6")
                .balanceInCents(0L)
                .user(user)
                .build();
    }

    @Test
    @DisplayName("Should register Pix key successfully when validations pass")
    void shouldRegisterPixKeySuccessfully() {
        when(accountDatabasePort.findById(accountId)).thenReturn(Optional.of(account));
        when(pixKeyDatabasePort.existsByValue(keyValue)).thenReturn(false);
        when(pixKeyDatabasePort.save(any(PixKey.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PixKey result = registerPixKeyService.register(accountId, keyType, keyValue);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getType()).isEqualTo(keyType);
        assertThat(result.getValue()).isEqualTo(keyValue);
        assertThat(result.getAccount().getId()).isEqualTo(accountId);
        assertThat(result.getCreatedAt()).isNotNull();

        verify(accountDatabasePort).findById(accountId);
        verify(pixKeyDatabasePort).existsByValue(keyValue);
        verify(pixKeyDatabasePort).save(any(PixKey.class));
    }

    @Test
    @DisplayName("Should throw exception when account is not found")
    void shouldThrowExceptionWhenAccountNotFound() {
        when(accountDatabasePort.findById(accountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registerPixKeyService.register(accountId, keyType, keyValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Account not found");

        verify(pixKeyDatabasePort, never()).existsByValue(any());
        verify(pixKeyDatabasePort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when Pix key already exists")
    void shouldThrowExceptionWhenPixKeyAlreadyExists() {
        when(accountDatabasePort.findById(accountId)).thenReturn(Optional.of(account));
        when(pixKeyDatabasePort.existsByValue(keyValue)).thenReturn(true);

        assertThatThrownBy(() -> registerPixKeyService.register(accountId, keyType, keyValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pix key already exists");

        verify(pixKeyDatabasePort, never()).save(any());
    }
}