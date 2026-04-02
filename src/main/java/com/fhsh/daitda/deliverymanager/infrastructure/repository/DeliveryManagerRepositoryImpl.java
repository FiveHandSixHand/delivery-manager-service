package com.fhsh.daitda.deliverymanager.infrastructure.repository;

import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
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
    public DeliveryManager save(DeliveryManager deliveryManager) {
        return deliveryManagerJpaRepository.save(deliveryManager);
    }
}
