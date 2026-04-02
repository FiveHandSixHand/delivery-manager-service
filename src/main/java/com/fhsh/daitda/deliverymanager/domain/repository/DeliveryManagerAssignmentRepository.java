package com.fhsh.daitda.deliverymanager.domain.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;

import java.util.Optional;
import java.util.UUID;

// 배송담당자 배정용 조회 레포지토리
public interface DeliveryManagerAssignmentRepository {

    // type과 hubId 기준으로 다음 배송담당자 조회
    Optional<DeliveryManager> findNextAssignable(
            DeliveryManagerType type, UUID hubId, int startSequence);
}
