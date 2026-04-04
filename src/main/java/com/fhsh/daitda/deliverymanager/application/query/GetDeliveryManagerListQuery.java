package com.fhsh.daitda.deliverymanager.application.query;

import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.presentation.dto.request.GetDeliveryManagerListRequest;

import java.util.UUID;

public record GetDeliveryManagerListQuery(
        DeliveryManagerType type,
        UUID hubId,
        String sortBy
) {
    public static GetDeliveryManagerListQuery from(GetDeliveryManagerListRequest request) {
        return new GetDeliveryManagerListQuery(
                request.getType(),
                request.getHubId(),
                request.getSortBy() == null ? "createdAt" : request.getSortBy()
        );
    }
}
