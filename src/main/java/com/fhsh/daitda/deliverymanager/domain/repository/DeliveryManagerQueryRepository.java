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

    // 배송담당자 생성 시 중복 생성을 막기 위해 배송담당자로 등록되었는 지 확인
    boolean existsByUserId(UUID userId);

    // 배송담당자 생성 시 다음 순번 반환
    Integer findLastSequence(DeliveryManagerType type, UUID hubId);
}
