package com.fastpay.application.service;

import com.fastpay.domain.exception.KeyNotFoundException;
import com.fastpay.domain.model.Account;
import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.model.User;
import com.fastpay.domain.model.enums.KeyType;
import com.fastpay.domain.port.out.PixKeyDatabasePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindPixKeyServiceTest {

    @Mock
    private PixKeyDatabasePort pixKeyDatabasePort;

    @InjectMocks
    private FindPixKeyService findPixKeyService;

    private PixKey pixKey;
    private String keyValue;

    @BeforeEach
    void setUp() {
        keyValue = "receiver@example.com";

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Receiver Name")
                .document("22222222222")
                .email("receiver@example.com")
                .build();

        Account account = Account.builder()
                .id(UUID.randomUUID())
                .agency("0001")
                .number("98765-4")
                .balanceInCents(0L)
                .user(user)
                .build();

        pixKey = PixKey.builder()
                .id(UUID.randomUUID())
                .type(KeyType.EMAIL)
                .value(keyValue)
                .createdAt(Instant.now())
                .account(account)
                .build();
    }

    @Test
    @DisplayName("Should return PixKey successfully when it exists in the database")
    void shouldFindPixKeySuccessfully() {
        when(pixKeyDatabasePort.findByValue(keyValue)).thenReturn(Optional.of(pixKey));

        PixKey result = findPixKeyService.findByValue(keyValue);

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(keyValue);
        assertThat(result.getType()).isEqualTo(KeyType.EMAIL);
        assertThat(result.getAccount().getUser().getName()).isEqualTo("Receiver Name");

        verify(pixKeyDatabasePort).findByValue(keyValue);
    }

    @Test
    @DisplayName("Should throw KeyNotFoundException when PixKey does not exist")
    void shouldThrowExceptionWhenPixKeyNotFound() {
        String nonExistentKey = "notfound@example.com";
        when(pixKeyDatabasePort.findByValue(nonExistentKey)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> findPixKeyService.findByValue(nonExistentKey))
                .isInstanceOf(KeyNotFoundException.class)
                .hasMessageContaining("Pix key not found");

        verify(pixKeyDatabasePort).findByValue(nonExistentKey);
    }
}