package com.fhsh.daitda.deliverymanager.application.result;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GetDeliveryManagerListResult(
        UUID deliveryManagerId,
        UUID userId,
        UUID hubId,
        String slackId,
        DeliveryManagerType type,
        int sequence
) {
    public static GetDeliveryManagerListResult from(DeliveryManager deliveryManager) {
        return GetDeliveryManagerListResult.builder()
                .deliveryManagerId(deliveryManager.getDeliveryManagerId())
                .userId(deliveryManager.getManagerInfo().getUserId())
                .hubId(deliveryManager.getManagerInfo().getHubId())
                .slackId(deliveryManager.getManagerInfo().getSlackId())
                .type(deliveryManager.getType())
                .sequence(deliveryManager.getSequence())
                .build();
    }
}
