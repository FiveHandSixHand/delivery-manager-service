package com.fhsh.daitda.deliverymanager.domain.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.AssignmentCursor;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;

import java.util.Optional;
import java.util.UUID;

public interface AssignmentCursorRepository {
    // 타입과 허브 아이디로 커서 조회
    Optional<AssignmentCursor> findByTypeAndHubId(DeliveryManagerType type, UUID hubId);

    AssignmentCursor save(AssignmentCursor assignmentCursor);
}
