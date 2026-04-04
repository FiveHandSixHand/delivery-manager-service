package com.fhsh.daitda.deliverymanager.presentation.dto.request;

import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class GetDeliveryManagerListRequest {
    private DeliveryManagerType type;
    private UUID hubId;
    private String sortBy = "createdAt";
}
