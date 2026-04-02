package com.fhsh.daitda.deliverymanager.domain.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerQueryRepository {

    // 단건 조회
    Optional<DeliveryManager> findById(UUID deliveryManagerId);

    // 생성일순 목록 조회
    List<DeliveryManager> findAllByHubIdAndTypeOrderByCreatedAtAsc(UUID hubId, DeliveryManagerType type);

    // 수정일순 목록 조회
    List<DeliveryManager> findAllByHubIdAndTypeOrderByUpdatedAtAsc(UUID hubId, DeliveryManagerType type);
}
