package com.fhsh.daitda.deliverymanager.infrastructure.messaging;

import com.fhsh.daitda.deliverymanager.application.port.event.DeliveryCompleteEvent;

import java.util.UUID;

public record DeliveryCompleteMessage(
        UUID deliveryId,
        UUID deliveryManagerId
) {
    public static DeliveryCompleteMessage from(DeliveryCompleteEvent event) {
        return new DeliveryCompleteMessage(
                event.deliveryId(),
                event.deliveryManagerId()
        );
    }
}
