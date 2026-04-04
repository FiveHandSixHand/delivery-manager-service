package com.fhsh.daitda.deliverymanager.application.result;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.entity.ManagerInfo;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record GetMyDeliveryManagerResult(
        UUID deliveryManagerId,
        UUID userId,
        UUID hubId,
        String slackId,
        DeliveryManagerType type,
        int sequence
) {
    public static GetMyDeliveryManagerResult from(DeliveryManager deliveryManager) {
        ManagerInfo managerInfo = deliveryManager.getManagerInfo();

        return GetMyDeliveryManagerResult.builder()
                .deliveryManagerId(deliveryManager.getDeliveryManagerId())
                .userId(managerInfo != null ? managerInfo.getUserId() : null)
                .hubId(managerInfo != null ? managerInfo.getHubId() : null)
                .slackId(managerInfo != null ? managerInfo.getSlackId() : null)
                .type(deliveryManager.getType())
                .sequence(deliveryManager.getSequence())
                .build();
    }
}
