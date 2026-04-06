package com.fhsh.daitda.deliverymanager.infrastructure.messaging.consumer;

import com.fhsh.daitda.deliverymanager.application.command.StartDeliveryCommand;
import com.fhsh.daitda.deliverymanager.application.service.command.DeliveryManagerCommandService;
import com.fhsh.daitda.deliverymanager.application.port.event.DeliveryStartEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryStartRequestConsumer {

    private final DeliveryManagerCommandService commandService;

    @KafkaListener(
            topics = "delivery.start.request",
            groupId = "delivery-manager-service"
    )
    public void consume(DeliveryStartEvent event) {
        commandService.startDelivery(new StartDeliveryCommand(event.deliveryId(), event.deliveryManagerId()));
    }
}
