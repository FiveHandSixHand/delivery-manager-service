package com.fhsh.daitda.deliverymanager.domain.repository;

import com.fhsh.daitda.deliverymanager.application.query.GetDeliveryManagerListQuery;
import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerQueryRepository {

    // 단건 조회
    Optional<DeliveryManager> findById(UUID deliveryManagerId);

    // 목록 조회
    Page<DeliveryManager> findAll(GetDeliveryManagerListQuery query, Pageable pageable);
}
