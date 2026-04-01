package com.fhsh.daitda.deliverymanager.domain.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepository {
    DeliveryManager save(DeliveryManager deliveryManager);

    Optional<DeliveryManager> findById(UUID deliveryManagerId);
}
