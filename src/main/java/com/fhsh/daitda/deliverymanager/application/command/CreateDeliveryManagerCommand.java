package com.fhsh.daitda.deliverymanager.application.command;

import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;

import java.util.UUID;

public record CreateDeliveryManagerCommand(
        UUID targetUserId,
        DeliveryManagerType type
) { }
