package com.fhsh.daitda.deliverymanager.application.service.query;

import com.fhsh.daitda.deliverymanager.application.query.GetDeliveryManagerListQuery;
import com.fhsh.daitda.deliverymanager.application.result.GetDeliveryManagerListResult;
import com.fhsh.daitda.deliverymanager.domain.repository.DeliveryManagerQueryRepository;
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

    public Page<GetDeliveryManagerListResult> getDeliveryManagers(GetDeliveryManagerListQuery query, Pageable pageable) {
        return deliveryManagerQueryRepository.findAll(query, pageable)
                .map(GetDeliveryManagerListResult::from);
    }
}
