package com.fhsh.daitda.deliverymanager.application.command;

import java.util.UUID;

public record CompleteAssignmentCommand(
        UUID deliveryId,
        UUID hubId
) { }
