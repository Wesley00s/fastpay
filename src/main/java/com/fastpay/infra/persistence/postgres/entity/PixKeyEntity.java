package com.fastpay.infra.persistence.postgres.entity;

import com.fastpay.domain.model.enums.KeyType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pix_keys")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixKeyEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeyType type;

    @Column(unique = true, nullable = false)
    private String value;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private AccountEntity account;

}