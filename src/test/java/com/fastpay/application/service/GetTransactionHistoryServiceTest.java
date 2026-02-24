package com.fastpay.application.service;

import com.fastpay.domain.model.Account;
import com.fastpay.domain.model.Transaction;
import com.fastpay.domain.model.User;
import com.fastpay.domain.model.enums.TransactionStatus;
import com.fastpay.domain.pagination.PageResult;
import com.fastpay.domain.port.out.AccountDatabasePort;
import com.fastpay.domain.port.out.TransactionDatabasePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTransactionHistoryServiceTest {

    @Mock
    private TransactionDatabasePort transactionDatabasePort;

    @Mock
    private AccountDatabasePort accountDatabasePort;

    @InjectMocks
    private GetTransactionHistoryService getTransactionHistoryService;

    private Account account;
    private PageResult<Transaction> pageResult;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .name("History User")
                .email("history@example.com")
                .build();

        account = Account.builder()
                .id(UUID.randomUUID())
                .agency("0001")
                .number("11111-1")
                .balanceInCents(5000L)
                .user(user)
                .build();

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .amountInCents(1000L)
                .sender(account)
                .receiver(account).status(TransactionStatus.COMPLETED)
                .timestamp(Instant.now())
                .build();

        pageResult = new PageResult<>(
                List.of(transaction),
                0,
                1,
                1L
        );
    }

    @Test
    @DisplayName("Should return a paginated history of transactions successfully")
    void shouldReturnHistorySuccessfully() {
        int page = 0;
        int size = 10;

        when(accountDatabasePort.findById(account.getId())).thenReturn(Optional.of(account));
        when(transactionDatabasePort.findHistoryByAccountId(account.getId(), page, size)).thenReturn(pageResult);

        PageResult<Transaction> result = getTransactionHistoryService.getHistory(account.getId(), page, size);

        assertThat(result).isNotNull();
        assertThat(result.data()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.currentPage()).isZero();

        verify(accountDatabasePort).findById(account.getId());
        verify(transactionDatabasePort).findHistoryByAccountId(account.getId(), page, size);
    }

    @Test
    @DisplayName("Should throw exception when account is not found before fetching history")
    void shouldThrowExceptionWhenAccountNotFound() {
        UUID invalidAccountId = UUID.randomUUID();
        int page = 0;
        int size = 10;

        when(accountDatabasePort.findById(invalidAccountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getTransactionHistoryService.getHistory(invalidAccountId, page, size))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Account not found for ID");

        verify(accountDatabasePort).findById(invalidAccountId);
        verify(transactionDatabasePort, never()).findHistoryByAccountId(invalidAccountId, page, size);
    }
}