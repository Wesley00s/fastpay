package com.fastpay.application.service;

import com.fastpay.domain.model.Account;
import com.fastpay.domain.model.User;
import com.fastpay.domain.port.out.AccountDatabasePort;
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
class AccountOperationServiceTest {

    @Mock
    private AccountDatabasePort accountDatabasePort;

    @InjectMocks
    private AccountOperationService accountOperationService;

    private Account account;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .build();

        account = Account.builder()
                .id(UUID.randomUUID())
                .agency("0001")
                .number("12345-6")
                .balanceInCents(1000L).user(user)
                .build();
    }

    @Test
    @DisplayName("Should process deposit successfully when amount is positive")
    void shouldDepositSuccessfully() {
        Long depositAmount = 5000L;
        when(accountDatabasePort.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountDatabasePort.update(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        Account updatedAccount = accountOperationService.deposit(account.getId(), depositAmount);

        assertThat(updatedAccount.getBalanceInCents()).isEqualTo(6000L);
        verify(accountDatabasePort).update(account);
    }

    @Test
    @DisplayName("Should throw exception when account is not found for deposit")
    void shouldThrowExceptionWhenAccountNotFoundForDeposit() {
        UUID invalidId = UUID.randomUUID();
        when(accountDatabasePort.findById(invalidId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountOperationService.deposit(invalidId, 1000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Account not found");

        verify(accountDatabasePort, never()).update(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when deposit amount is invalid")
    void shouldThrowExceptionWhenDepositAmountIsInvalid() {
        when(accountDatabasePort.findById(account.getId())).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountOperationService.deposit(account.getId(), -500L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount must be positive");

        verify(accountDatabasePort, never()).update(any());
    }

    @Test
    @DisplayName("Should return account details successfully when requested by email")
    void shouldGetDetailsByEmailSuccessfully() {
        String email = "john@example.com";
        when(accountDatabasePort.findByUserEmail(email)).thenReturn(Optional.of(account));

        Account result = accountOperationService.getDetailsByEmail(email);

        assertThat(result).isNotNull();
        assertThat(result.getUser().getEmail()).isEqualTo(email);
        assertThat(result.getBalanceInCents()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("Should throw exception when account is not found by email")
    void shouldThrowExceptionWhenAccountNotFoundByEmail() {
        String invalidEmail = "notfound@example.com";
        when(accountDatabasePort.findByUserEmail(invalidEmail)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountOperationService.getDetailsByEmail(invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Account not found for the authenticated user");
    }
}