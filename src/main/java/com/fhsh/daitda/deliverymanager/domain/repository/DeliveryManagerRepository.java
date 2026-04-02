package com.fhsh.daitda.deliverymanager.domain.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepository {
    // 배송담당자 아이디로 배송담당자 조회
    Optional<DeliveryManager> findById(UUID deliveryManagerId);

    // 배송담당자 생성 시 중복 생성을 막기 위해 배송담당자로 등록되었는 지 확인
    boolean existsByUserId(UUID userId);

    // 배송담당자 생성 시 다음 순번 반환
    Integer findLastSequence(DeliveryManagerType type, UUID hubId);

    DeliveryManager save(DeliveryManager deliveryManager);
}
