package com.fhsh.daitda.deliverymanager.infrastructure.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerJpaRepository extends JpaRepository<DeliveryManager, UUID> {

    Optional<DeliveryManager> findByDeliveryManagerIdAndDeletedAtIsNull(UUID deliveryManagerId);
}
