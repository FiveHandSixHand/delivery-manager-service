package com.fhsh.daitda.deliverymanager.domain.entity;

import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.domain.BaseUserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Table(name = "p_delivery_managers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryManager extends BaseUserEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false)
    private UUID deliveryManagersId;

    @Embedded
    private ManagerInfo managerInfo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private DeliveryManagerType type;

    @Column(nullable = false)
    private int sequence;

    @Column(nullable = false)
    private boolean isDelivery;

    @Builder
    public DeliveryManager(UUID userId, UUID hubId, String slackId, DeliveryManagerType type, int sequence, boolean isDelivery) {
        this.managerInfo = new ManagerInfo(userId, hubId, slackId);

        this.type = type;

        if (sequence < 1) {
            throw new IllegalArgumentException("sequence는 1 이상이어야 합니다.");
        }
        this.sequence = sequence;

        this.isDelivery = isDelivery;
    }
}
