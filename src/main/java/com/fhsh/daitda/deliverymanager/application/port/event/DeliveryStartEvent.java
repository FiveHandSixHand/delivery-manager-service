package com.fhsh.daitda.deliverymanager.application.port.event;

import java.util.UUID;

public record DeliveryStartEvent(
        UUID deliveryId,
        UUID deliveryManagerId
) { }
