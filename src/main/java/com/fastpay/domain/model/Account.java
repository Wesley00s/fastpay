package com.fastpay.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private UUID id;
    private String agency;
    private String number;
    private Long balanceInCents;
    private User user;

    public void debit(Long amountInCents) {
        if (amountInCents <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (this.balanceInCents < amountInCents) {
            throw new IllegalStateException("Insufficient balance");
        }
        this.balanceInCents -= amountInCents;
    }

    public void credit(Long amountInCents) {
        if (amountInCents <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.balanceInCents += amountInCents;
    }

}
