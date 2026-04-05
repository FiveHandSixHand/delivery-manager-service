package com.fhsh.daitda.deliverymanager.presentation.dto.request;

import java.util.UUID;

public record CompleteAssignmentRequest(
    UUID deliveryId,
    UUID hubId
) { }
