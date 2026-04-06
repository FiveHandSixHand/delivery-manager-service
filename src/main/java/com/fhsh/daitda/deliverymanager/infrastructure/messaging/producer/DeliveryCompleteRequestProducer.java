package com.fhsh.daitda.deliverymanager.infrastructure.messaging.producer;

import com.fhsh.daitda.deliverymanager.application.port.DeliveryCompleteEventPort;
import com.fhsh.daitda.deliverymanager.application.port.event.DeliveryCompleteEvent;
import com.fhsh.daitda.deliverymanager.infrastructure.messaging.DeliveryCompleteMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryCompleteRequestProducer implements DeliveryCompleteEventPort {

    private final KafkaTemplate<String, DeliveryCompleteMessage> kafkaTemplate;

    @Override
    public void send(DeliveryCompleteEvent event) {
        DeliveryCompleteMessage message = DeliveryCompleteMessage.from(event);
        kafkaTemplate.send("delivery.complete.request", event.deliveryId().toString(), message);
    }
}
