package com.fhsh.daitda.deliverymanager.application.service.query;

import com.fhsh.daitda.deliverymanager.application.query.GetDeliveryManagerListQuery;
import com.fhsh.daitda.deliverymanager.application.query.GetDeliveryManagerQuery;
import com.fhsh.daitda.deliverymanager.application.result.GetDeliveryManagerListResult;
import com.fhsh.daitda.deliverymanager.application.result.GetDeliveryManagerResult;
import com.fhsh.daitda.deliverymanager.domain.exception.DeliveryManagerErrorCode;
import com.fhsh.daitda.deliverymanager.domain.repository.DeliveryManagerQueryRepository;
import com.fhsh.daitda.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryManagerQueryService {

    private final DeliveryManagerQueryRepository deliveryManagerQueryRepository;

    // 배송담당자 단건 조회
    public GetDeliveryManagerResult getDeliveryManager(GetDeliveryManagerQuery query) {
        return deliveryManagerQueryRepository.findById(query.deliveryManagerId())
                .map(GetDeliveryManagerResult::from)
                .orElseThrow(() -> new BusinessException(DeliveryManagerErrorCode.DELIVERY_MANAGER_NOT_FOUND));
    }

    // 배송담당자 목록 조회
    public Page<GetDeliveryManagerListResult> getDeliveryManagers(GetDeliveryManagerListQuery query, Pageable pageable) {
        return deliveryManagerQueryRepository.findAll(query, pageable)
                .map(GetDeliveryManagerListResult::from);
    }
}
