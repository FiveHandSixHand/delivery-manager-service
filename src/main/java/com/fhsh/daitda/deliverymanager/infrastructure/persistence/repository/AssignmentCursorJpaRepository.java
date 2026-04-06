package com.fhsh.daitda.deliverymanager.infrastructure.persistence.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.AssignmentCursor;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssignmentCursorJpaRepository extends JpaRepository<AssignmentCursor, UUID> {

    Optional<AssignmentCursor> findByTypeAndHubId(DeliveryManagerType type, UUID hubId);
}
