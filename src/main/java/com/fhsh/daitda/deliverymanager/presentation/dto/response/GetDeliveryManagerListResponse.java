package com.fhsh.daitda.deliverymanager.presentation.dto.response;

import com.fhsh.daitda.deliverymanager.application.result.GetDeliveryManagerListResult;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record GetDeliveryManagerListResponse(
        UUID deliveryManagerId,
        UUID userId,
        UUID hubId,
        String slackId,
        DeliveryManagerType type,
        int sequence
) {
    public static GetDeliveryManagerListResponse from(GetDeliveryManagerListResult result) {
        return GetDeliveryManagerListResponse.builder()
                .deliveryManagerId(result.deliveryManagerId())
                .userId(result.userId())
                .hubId(result.hubId())
                .slackId(result.slackId())
                .type(result.type())
                .sequence(result.sequence())
                .build();
    }
}
