package com.fhsh.daitda.deliverymanager.application.result;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.entity.ManagerInfo;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CompleteAssignmentResult(
        UUID deliveryId,
        UUID deliveryManagerId,
        UUID userId,
        UUID hubId,
        String slackId,
        DeliveryManagerType type,
        int sequence
) {
    public static CompleteAssignmentResult from(DeliveryManager deliveryManager, UUID deliveryId) {
        ManagerInfo managerInfo = deliveryManager.getManagerInfo();

        return CompleteAssignmentResult.builder()
                .deliveryId(deliveryId)
                .deliveryManagerId(deliveryManager.getDeliveryManagerId())
                .userId(managerInfo != null ? managerInfo.getUserId() : null)
                .hubId(managerInfo != null ? managerInfo.getHubId() : null)
                .slackId(managerInfo != null ? managerInfo.getSlackId() : null)
                .type(deliveryManager.getType())
                .sequence(deliveryManager.getSequence())
                .build();
    }
}
