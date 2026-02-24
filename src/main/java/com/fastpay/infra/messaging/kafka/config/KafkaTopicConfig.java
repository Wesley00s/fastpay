package com.fastpay.infra.messaging.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String SETTLEMENT_TOPIC = "pix-settlement-topic";

    @Bean
    public NewTopic settlementTopic() {
        return TopicBuilder.name(SETTLEMENT_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}