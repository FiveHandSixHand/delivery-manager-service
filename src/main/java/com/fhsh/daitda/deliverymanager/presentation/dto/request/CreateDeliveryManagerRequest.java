package com.fhsh.daitda.deliverymanager.presentation.dto.request;

import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;

import java.util.UUID;

public record CreateDeliveryManagerRequest(
    UUID targetUserId,
    DeliveryManagerType type
) { }
