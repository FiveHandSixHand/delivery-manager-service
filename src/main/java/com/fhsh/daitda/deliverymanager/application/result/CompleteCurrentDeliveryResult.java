package com.fhsh.daitda.deliverymanager.application.result;

import java.util.UUID;

public record CompleteCurrentDeliveryResult(
    UUID deliveryId,
    boolean isDelivery
) { }
