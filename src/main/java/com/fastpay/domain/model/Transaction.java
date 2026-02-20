package com.fastpay.domain.model;

import com.fastpay.domain.model.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private UUID id;
    private Long amountInCents;
    private TransactionStatus status;
    private Instant timestamp;
    private Account sender;
    private Account receiver;

}
