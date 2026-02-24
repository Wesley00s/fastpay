package com.fastpay.application.service;

import com.fastpay.domain.exception.KeyNotFoundException;
import com.fastpay.domain.model.Account;
import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.model.Transaction;
import com.fastpay.domain.model.User;
import com.fastpay.domain.model.enums.KeyType;
import com.fastpay.domain.port.out.AccountDatabasePort;
import com.fastpay.domain.port.out.PixKeyDatabasePort;
import com.fastpay.domain.port.out.SettlementMessagingPort;
import com.fastpay.domain.port.out.TransactionDatabasePort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendPixServiceTest {

    @Mock
    private AccountDatabasePort accountDatabasePort;

    @Mock
    private PixKeyDatabasePort pixKeyDatabasePort;

    @Mock
    private TransactionDatabasePort transactionDatabasePort;

    @Mock
    private SettlementMessagingPort settlementMessagingPort;

    @InjectMocks
    private SendPixService sendPixService;

    private Account senderAccount;
    private Account receiverAccount;
    private PixKey destinationPixKey;

    @BeforeEach
    void setUp() {
        User senderUser = User.builder()
                .id(UUID.randomUUID())
                .name("Sender Name")
                .document("11111111111")
                .email("sender@example.com")
                .password("password")
                .build();

        User receiverUser = User.builder()
                .id(UUID.randomUUID())
                .name("Receiver Name")
                .document("22222222222")
                .email("receiver@example.com")
                .password("password")
                .build();

        senderAccount = Account.builder()
                .id(UUID.randomUUID())
                .agency("0001")
                .number("12345-6")
                .balanceInCents(50000L)
                .user(senderUser)
                .build();

        receiverAccount = Account.builder()
                .id(UUID.randomUUID())
                .agency("0001")
                .number("98765-4")
                .balanceInCents(1000L)
                .user(receiverUser)
                .build();

        destinationPixKey = PixKey.builder()
                .id(UUID.randomUUID())
                .type(KeyType.PHONE)
                .value("+5511999999999")
                .createdAt(Instant.now())
                .account(receiverAccount)
                .build();
    }

    @Test
    @DisplayName("Should process transfer successfully when all validations pass")
    void shouldProcessTransferSuccessfully() {
        Long transferAmount = 15000L;
        String targetKeyValue = "+5511999999999";

        when(accountDatabasePort.findById(senderAccount.getId())).thenReturn(Optional.of(senderAccount));
        when(pixKeyDatabasePort.findByValue(targetKeyValue)).thenReturn(Optional.of(destinationPixKey));
        when(transactionDatabasePort.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = sendPixService.send(senderAccount.getId(), targetKeyValue, transferAmount);

        assertThat(result).isNotNull();
        assertThat(result.getAmountInCents()).isEqualTo(transferAmount);
        assertThat(result.getSender().getId()).isEqualTo(senderAccount.getId());
        assertThat(result.getReceiver().getId()).isEqualTo(receiverAccount.getId());

        assertThat(senderAccount.getBalanceInCents()).isEqualTo(35000L);

        verify(accountDatabasePort).update(senderAccount);
        verify(transactionDatabasePort).save(any(Transaction.class));
        verify(settlementMessagingPort).sendSettlementEvent(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when sender has insufficient funds")
    void shouldThrowExceptionWhenInsufficientFunds() {
        Long transferAmount = 60000L;
        String targetKeyValue = "+5511999999999";

        when(accountDatabasePort.findById(senderAccount.getId())).thenReturn(Optional.of(senderAccount));
        when(pixKeyDatabasePort.findByValue(targetKeyValue)).thenReturn(Optional.of(destinationPixKey));

        assertThatThrownBy(() -> sendPixService.send(senderAccount.getId(), targetKeyValue, transferAmount))
                .isInstanceOf(com.fastpay.domain.exception.InsufficientBalanceException.class)
                .hasMessageContaining("Insufficient balance");

        verify(accountDatabasePort, never()).update(any());
        verify(transactionDatabasePort, never()).save(any());
        verify(settlementMessagingPort, never()).sendSettlementEvent(any());
    }

    @Test
    @DisplayName("Should throw KeyNotFoundException when destination key does not exist")
    void shouldThrowExceptionWhenDestinationKeyNotFound() {
        Long transferAmount = 1000L;
        String invalidKeyValue = "notfound@example.com";

        when(accountDatabasePort.findById(senderAccount.getId())).thenReturn(Optional.of(senderAccount));
        when(pixKeyDatabasePort.findByValue(invalidKeyValue)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sendPixService.send(senderAccount.getId(), invalidKeyValue, transferAmount))
                .isInstanceOf(KeyNotFoundException.class);

        verify(accountDatabasePort, never()).update(any());
        verify(transactionDatabasePort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when trying to transfer to own account")
    void shouldThrowExceptionWhenTransferringToSelf() {
        Long transferAmount = 1000L;
        String ownKeyValue = "sender@example.com";

        PixKey ownPixKey = PixKey.builder()
                .id(UUID.randomUUID())
                .type(KeyType.EMAIL)
                .value(ownKeyValue)
                .createdAt(Instant.now())
                .account(senderAccount)
                .build();

        when(accountDatabasePort.findById(senderAccount.getId())).thenReturn(Optional.of(senderAccount));
        when(pixKeyDatabasePort.findByValue(ownKeyValue)).thenReturn(Optional.of(ownPixKey));

        assertThatThrownBy(() -> sendPixService.send(senderAccount.getId(), ownKeyValue, transferAmount))
                .isInstanceOf(IllegalArgumentException.class);

        verify(accountDatabasePort, never()).update(any());
        verify(transactionDatabasePort, never()).save(any());
    }
}