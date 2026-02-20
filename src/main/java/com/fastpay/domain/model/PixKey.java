package com.fastpay.domain.model;

import com.fastpay.domain.model.enums.KeyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixKey {
    private UUID id;
    private KeyType type;
    private String value;
    private java.time.Instant createdAt;
    private Account account;

}
