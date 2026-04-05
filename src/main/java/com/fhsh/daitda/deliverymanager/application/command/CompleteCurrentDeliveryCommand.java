package com.fhsh.daitda.deliverymanager.application.command;

import java.util.UUID;

public record CompleteCurrentDeliveryCommand(
        UUID deliveryId,
        UUID userId
) { }
