package com.fhsh.daitda.deliverymanager.domain.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepository {
    // 배송담당자 아이디로 배송담당자 조회
    Optional<DeliveryManager> findById(UUID deliveryManagerId);

    DeliveryManager save(DeliveryManager deliveryManager);
}
