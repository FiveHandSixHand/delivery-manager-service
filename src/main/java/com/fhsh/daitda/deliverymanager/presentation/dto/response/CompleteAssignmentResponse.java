package com.fhsh.daitda.deliverymanager.presentation.dto.response;

import com.fhsh.daitda.deliverymanager.application.result.CompleteAssignmentResult;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CompleteAssignmentResponse(
        UUID deliveryId,
        UUID deliveryManagerId,
        UUID userId,
        UUID hubId,
        String slackId,
        DeliveryManagerType type,
        int sequence
) {
    public static CompleteAssignmentResponse from(CompleteAssignmentResult result) {
        return CompleteAssignmentResponse.builder()
                .deliveryId(result.deliveryId())
                .deliveryManagerId(result.deliveryManagerId())
                .userId(result.userId())
                .hubId(result.hubId())
                .slackId(result.slackId())
                .type(result.type())
                .sequence(result.sequence())
                .build();
    }
}
