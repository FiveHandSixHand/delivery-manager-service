package com.fhsh.daitda.deliverymanager.application.command;

import java.util.UUID;

public record StartDeliveryCommand(
        UUID deliveryId,
        UUID deliveryManagerId
) { }
