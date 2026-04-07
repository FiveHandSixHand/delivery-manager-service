package com.fhsh.daitda.deliverymanager.infrastructure.messaging.producer;

import com.fhsh.daitda.deliverymanager.application.port.DeliveryCompleteEventPort;
import com.fhsh.daitda.deliverymanager.application.port.event.DeliveryCompleteEvent;
import com.fhsh.daitda.deliverymanager.infrastructure.config.KafkaTopicConfig;
import com.fhsh.daitda.deliverymanager.infrastructure.messaging.DeliveryCompleteMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryCompleteRequestProducer implements DeliveryCompleteEventPort {

    private final KafkaTemplate<String, DeliveryCompleteMessage> kafkaTemplate;

    @Override
    public void send(DeliveryCompleteEvent event) {
        DeliveryCompleteMessage message = DeliveryCompleteMessage.from(event);
        kafkaTemplate
                .send(KafkaTopicConfig.DELIVERY_COMPLETE_REQUEST, event.deliveryId().toString(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka publish failed. topic={}, deliveryId={}", KafkaTopicConfig.DELIVERY_COMPLETE_REQUEST, event.deliveryId(), ex);
                    }
                });
    }
}
