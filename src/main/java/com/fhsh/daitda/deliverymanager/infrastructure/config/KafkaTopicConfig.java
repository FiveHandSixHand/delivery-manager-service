package com.fhsh.daitda.deliverymanager.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String DELIVERY_START_REQUEST = "delivery.start.request";
    public static final String DELIVERY_COMPLETE_REQUEST = "delivery.complete.request";

    @Bean
    public NewTopic deliveryStartRequestTopic() {
        return TopicBuilder.name(DELIVERY_START_REQUEST)
                .partitions(1) // 병렬 처리 계획이 없으므로 1로 설정
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deliveryCompleteRequestTopic() {
        return TopicBuilder.name(DELIVERY_COMPLETE_REQUEST)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
