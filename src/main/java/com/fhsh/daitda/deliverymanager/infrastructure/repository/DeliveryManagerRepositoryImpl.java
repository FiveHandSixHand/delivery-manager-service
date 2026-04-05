package com.fhsh.daitda.deliverymanager.infrastructure.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.repository.DeliveryManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryManagerRepositoryImpl implements DeliveryManagerRepository {

    private final DeliveryManagerJpaRepository deliveryManagerJpaRepository;

    @Override
    public Optional<DeliveryManager> findById(UUID deliveryManagerId) {
        return deliveryManagerJpaRepository.findByDeliveryManagerIdAndDeletedAtIsNull(deliveryManagerId);
    }

    @Override
    public Optional<DeliveryManager> findByUserId(UUID userId) {
        return deliveryManagerJpaRepository.findByManagerInfoUserIdAndDeletedAtIsNull(userId);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return deliveryManagerJpaRepository.existsByManagerInfo_UserIdAndDeletedAtIsNull(userId);
    }

    @Override
    public Integer findLastSequence(DeliveryManagerType type, UUID hubId) {
        return deliveryManagerJpaRepository
                .findFirstByTypeAndManagerInfo_HubIdAndDeletedAtIsNullOrderBySequenceDesc(type, hubId)
                .map(DeliveryManager::getSequence)
                .orElse(0);
    }

    @Override
    public DeliveryManager save(DeliveryManager deliveryManager) {
        return deliveryManagerJpaRepository.save(deliveryManager);
    }
}
