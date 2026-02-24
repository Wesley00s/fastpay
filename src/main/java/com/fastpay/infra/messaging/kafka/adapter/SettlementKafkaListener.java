package com.fastpay.infra.messaging.kafka.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastpay.domain.port.in.ProcessSettlementUseCase;
import com.fastpay.infra.messaging.kafka.config.KafkaTopicConfig;
import com.fastpay.infra.messaging.kafka.dto.SettlementEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementKafkaListener {

    private final ProcessSettlementUseCase processSettlementUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopicConfig.SETTLEMENT_TOPIC, groupId = "fastpay-settlement-group")
    public void consume(String payload) {
        log.info("Received message from Kafka topic {}: {}", KafkaTopicConfig.SETTLEMENT_TOPIC, payload);

        try {
            SettlementEvent event = objectMapper.readValue(payload, SettlementEvent.class);
            processSettlementUseCase.process(event.transactionId(), event.status());
        } catch (Exception e) {
            log.error("Failed to process settlement event payload: {}", payload, e);
        }
    }
}