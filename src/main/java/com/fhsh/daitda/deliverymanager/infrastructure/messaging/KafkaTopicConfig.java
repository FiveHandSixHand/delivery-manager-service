package com.fhsh.daitda.deliverymanager.infrastructure.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic deliveryStartRequestTopic() {
        return TopicBuilder.name("delivery.start.request")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deliveryCompleteRequestTopic() {
        return TopicBuilder.name("delivery.complete.request")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
