package com.fhsh.daitda.deliverymanager.infrastructure.external.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID userId,
        String email,
        String name,
        String role,
        String status,
        String slackUserId,
        UUID hubId,
        UUID companyId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
