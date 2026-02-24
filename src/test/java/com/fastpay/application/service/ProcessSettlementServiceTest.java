package com.fastpay.application.service;

import com.fastpay.domain.model.Transaction;
import com.fastpay.domain.model.enums.TransactionStatus;
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
class ProcessSettlementServiceTest {

    @Mock
    private TransactionDatabasePort transactionDatabasePort;

    @InjectMocks
    private ProcessSettlementService processSettlementService;

    private Transaction processingTransaction;

    @BeforeEach
    void setUp() {
        processingTransaction = Transaction.builder()
                .id(UUID.randomUUID())
                .amountInCents(2000L)
                .status(TransactionStatus.PROCESSING)
                .timestamp(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should process settlement successfully and update transaction status to COMPLETED")
    void shouldProcessSettlementSuccessfully() {
        when(transactionDatabasePort.findById(processingTransaction.getId())).thenReturn(Optional.of(processingTransaction));

        processSettlementService.process(processingTransaction.getId(), "PROCESSING");

        assertThat(processingTransaction.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        verify(transactionDatabasePort).save(processingTransaction);
    }

    @Test
    @DisplayName("Should throw exception when transaction is not found")
    void shouldThrowExceptionWhenTransactionNotFound() {
        UUID invalidTransactionId = UUID.randomUUID();
        when(transactionDatabasePort.findById(invalidTransactionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processSettlementService.process(invalidTransactionId, "PROCESSING"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Transaction not found");

        verify(transactionDatabasePort, never()).save(any());
    }

    @Test
    @DisplayName("Should return early without saving if transaction status is not PROCESSING")
    void shouldReturnEarlyIfStatusIsNotProcessing() {
        processingTransaction.setStatus(TransactionStatus.COMPLETED);
        when(transactionDatabasePort.findById(processingTransaction.getId())).thenReturn(Optional.of(processingTransaction));

        processSettlementService.process(processingTransaction.getId(), "COMPLETED");

        verify(transactionDatabasePort, never()).save(any());
    }
}