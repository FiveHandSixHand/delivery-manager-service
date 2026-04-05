package com.fhsh.daitda.deliverymanager.infrastructure.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerJpaRepository extends JpaRepository<DeliveryManager, UUID> {

    Optional<DeliveryManager> findByDeliveryManagerIdAndDeletedAtIsNull(UUID deliveryManagerId);

    Optional<DeliveryManager> findByManagerInfoUserIdAndDeletedAtIsNull(UUID userId);

    boolean existsByManagerInfo_UserIdAndDeletedAtIsNull(UUID userId);

    Optional<DeliveryManager> findFirstByTypeAndManagerInfo_HubIdAndDeletedAtIsNullOrderBySequenceDesc(
            DeliveryManagerType type,
            UUID hubId
    );
}
