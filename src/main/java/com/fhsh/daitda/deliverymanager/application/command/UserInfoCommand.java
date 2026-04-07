package com.fhsh.daitda.deliverymanager.application.command;

import java.util.UUID;

public record UserInfoCommand(
        UUID userId,
        UUID hubId,
        String slackUserId,
        String role
) { }
