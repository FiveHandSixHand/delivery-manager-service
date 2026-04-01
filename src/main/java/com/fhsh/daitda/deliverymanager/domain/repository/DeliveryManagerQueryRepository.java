package com.fhsh.daitda.deliverymanager.domain.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;

import java.util.List;
import java.util.UUID;

public interface DeliveryManagerQueryRepository {
    List<DeliveryManager> findByTypeAndHubIdOrderBySequence(DeliveryManagerType type, UUID hubId);

    List<DeliveryManager> findByTypeAndHubIdStartingFromSequence(
            DeliveryManagerType type,
            UUID hubId,
            int startSequence
    );
}
