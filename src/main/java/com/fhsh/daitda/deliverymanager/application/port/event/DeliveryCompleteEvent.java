package com.fhsh.daitda.deliverymanager.application.port.event;

import java.util.UUID;

public record DeliveryCompleteEvent(
        UUID deliveryId,
        UUID deliveryManagerId
) { }
