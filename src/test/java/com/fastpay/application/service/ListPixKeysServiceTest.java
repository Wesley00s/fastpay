package com.fastpay.application.service;

import com.fastpay.domain.model.Account;
import com.fastpay.domain.pagination.PageResult;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListPixKeysServiceTest {

    @Mock
    private PixKeyDatabasePort pixKeyDatabasePort;

    @InjectMocks
    private ListPixKeysService listPixKeysService;

    private UUID accountId;
    private PixKey pixKey;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("List Keys User")
                .email("listkeys@example.com")
                .build();

        Account account = Account.builder()
                .id(accountId)
                .agency("0001")
                .number("22222-2")
                .balanceInCents(0L)
                .user(user)
                .build();

        pixKey = PixKey.builder()
                .id(UUID.randomUUID())
                .type(KeyType.EMAIL)
                .value("listkeys@example.com")
                .createdAt(Instant.now())
                .account(account)
                .build();
    }

    @Test
    @DisplayName("Should return a paginated list of Pix keys successfully")
    void shouldReturnPaginatedPixKeysSuccessfully() {
        int page = 0;
        int size = 10;
        PageResult<PixKey> pageResult = new PageResult<>(List.of(pixKey), page, 1, 1L);

        when(pixKeyDatabasePort.findByAccountId(accountId, page, size)).thenReturn(pageResult);

        PageResult<PixKey> result = listPixKeysService.listByAccountId(accountId, page, size);

        assertThat(result).isNotNull();
        assertThat(result.data()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.data().getFirst().getValue()).isEqualTo("listkeys@example.com");

        verify(pixKeyDatabasePort).findByAccountId(accountId, page, size);
    }

    @Test
    @DisplayName("Should return an empty page when account has no Pix keys")
    void shouldReturnEmptyPageWhenNoKeysFound() {
        int page = 0;
        int size = 10;
        PageResult<PixKey> emptyPageResult = new PageResult<>(Collections.emptyList(), page, 0, 0L);

        when(pixKeyDatabasePort.findByAccountId(accountId, page, size)).thenReturn(emptyPageResult);

        PageResult<PixKey> result = listPixKeysService.listByAccountId(accountId, page, size);

        assertThat(result).isNotNull();
        assertThat(result.data()).isEmpty();
        assertThat(result.totalElements()).isZero();

        verify(pixKeyDatabasePort).findByAccountId(accountId, page, size);
    }
}