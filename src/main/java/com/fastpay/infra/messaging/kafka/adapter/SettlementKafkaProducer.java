package com.fastpay.infra.messaging.kafka.adapter;

import com.fastpay.domain.model.Transaction;
import com.fastpay.domain.port.out.SettlementMessagingPort;
import com.fastpay.infra.messaging.kafka.config.KafkaTopicConfig;
import com.fastpay.infra.messaging.kafka.dto.SettlementEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementKafkaProducer implements SettlementMessagingPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendSettlementEvent(Transaction transaction) {
        log.info("Preparing settlement event for transaction ID: {}", transaction.getId());

        SettlementEvent event = new SettlementEvent(
                transaction.getId(),
                transaction.getStatus().name()
        );

        kafkaTemplate.send(KafkaTopicConfig.SETTLEMENT_TOPIC, transaction.getId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully sent settlement event for transaction ID: {}", transaction.getId());
                    } else {
                        log.error("Failed to send settlement event for transaction ID: {}", transaction.getId(), ex);
                    }
                });
    }
}