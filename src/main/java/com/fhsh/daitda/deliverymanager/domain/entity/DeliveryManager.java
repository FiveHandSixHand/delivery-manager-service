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

/*
 * 배송담당자는 허브 배송 담당자와 업체 배송 담당자로 구분됩니다.
 * MASTER 배송담당자를 생성/수정/삭제/조회할 수 있습니다.
 * HUB 관리자는 담당 허브에 소속된 업체 배송 담당자만 생성/수정/삭제/조회할 수 있습니다.
 * 배송담당자는 본인의 정보만 조회할 수 있습니다.
 * 배송담당자는 배송 완료 처리를 수행할 수 있습니다. 이는 MASTER도 대신할 수 있습니다.
 * MASTER는 모든 배송담당자를 배정할 수 있으며, HUB 관리자는 담당 허브에 소속된 업체 배송 담당자를 배정할 수 있습니다.
 * 배송담당자는 배송 타입을 가져야 하며, 순서는 전체 허브 또는 소속된 각 허브에서 1~10번으로 구성됩니다.
 * 업체 배송 담당자는 소속된 허브의 아이디를 필수로 갖고 있어야 합니다.
 * 삭제된 배송 담당자는 배정 대상에서 제외합니다.
 */

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
    public DeliveryManager(UUID userId, UUID hubId, String slackId, DeliveryManagerType type, int sequence) {
        this.managerInfo = new ManagerInfo(userId, hubId, slackId);

        this.type = type;

        if (sequence < 1) {
            throw new IllegalArgumentException("sequence는 1 이상이어야 합니다.");
        }
        this.sequence = sequence;

        this.isDelivery = false;
    }
}
