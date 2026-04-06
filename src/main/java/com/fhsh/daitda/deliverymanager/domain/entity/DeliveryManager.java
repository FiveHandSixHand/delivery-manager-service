package com.fhsh.daitda.deliverymanager.domain.entity;

import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.exception.DeliveryManagerErrorCode;
import com.fhsh.daitda.domain.BaseUserEntity;
import com.fhsh.daitda.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
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
@Table(name = "p_delivery_managers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_delivery_manager_user_id",
                        columnNames = "user_id"
                )
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryManager extends BaseUserEntity {

    private static final int MIN_SEQUENCE = 1;
    private static final int MAX_SEQUENCE = 10;

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false)
    private UUID deliveryManagerId;

    private UUID deliveryId;

    @Embedded
    private ManagerInfo managerInfo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private DeliveryManagerType type;

    @Column(nullable = false)
    private int sequence;

    @Column(nullable = false)
    private boolean isDelivery;

    @Version
    private Long version;

    @Builder
    public DeliveryManager(UUID deliveryId, UUID userId, UUID hubId, String slackId, DeliveryManagerType type, int sequence, boolean isDelivery) {
        validate(type, hubId, sequence);

        this.deliveryId = deliveryId;
        this.managerInfo = ManagerInfo.of(userId, hubId, slackId);
        this.type = type;
        this.sequence = sequence;
        this.isDelivery = isDelivery;
    }

    // 배송담당자 생성
    public static DeliveryManager create(UUID userId, UUID hubId, String slackId,
                                         DeliveryManagerType type, int sequence) {
        return DeliveryManager.builder()
                .deliveryId(null)
                .userId(userId)
                .hubId(hubId)
                .slackId(slackId)
                .type(type)
                .sequence(sequence)
                .isDelivery(false)
                .build();
    }

    // 배송담당자의 슬랙 아이디 수정
    public void changeSlackId(String slackId) {
        this.managerInfo = this.managerInfo.withSlackId(slackId);
    }

    // 소속된 허브 아이디 수정
    public void changeHubId(UUID hubId) {
        validate(this.type, hubId, this.sequence);
        this.managerInfo = this.managerInfo.withHubId(hubId);
    }

    // 순서 수정
    public void changeSequence(int sequence) {
        validateSequence(sequence);
        this.sequence = sequence;
    }

    // 배송담당자 삭제
    public void delete(UUID deletedBy) {
        super.delete(deletedBy);
    }

    // 배송중 상태인지 확인하는 메서드
    public boolean isDelivering() {
        return this.isDelivery;
    }

    // 배송 시작으로 변경
    public void startDelivery(UUID deliveryId) {
        if (this.isDeleted()) {
            throw new BusinessException(DeliveryManagerErrorCode.DELETED_DELIVERY_MANAGER_CANNOT_CHANGE_STATUS);
        }
        if (this.isDelivery) {
            throw new BusinessException(DeliveryManagerErrorCode.DELIVERY_MANAGER_ALREADY_DELIVERING);
        }
        this.deliveryId = deliveryId;
        this.isDelivery = true;
    }

    // 배송 완료로 변경
    public void completeDelivery(UUID deliveryId) {
        if (this.isDeleted()) {
            throw new BusinessException(DeliveryManagerErrorCode.DELETED_DELIVERY_MANAGER_CANNOT_CHANGE_STATUS);
        }
        if (!this.isDelivery) {
            throw new BusinessException(DeliveryManagerErrorCode.DELIVERY_MANAGER_NOT_DELIVERING);
        }
        if (!Objects.equals(this.deliveryId, deliveryId)) {
            throw new BusinessException(DeliveryManagerErrorCode.NOT_CURRENT_DELIVERY);
        }

        this.deliveryId = null;
        this.isDelivery = false;
    }

    // 배정 가능한 상태인지 확인하는 메서드
    public boolean isAssignable() {
        return !isDeleted() && !isDelivery;
    }

    // 해당 허브 소속인지 확인하는 메서드
    public boolean belongsTo(UUID hubId) {
        return this.managerInfo.belongsTo(hubId);
    }

    // 입력 값 검증
    private void validate(DeliveryManagerType type, UUID hubId, int sequence) {
        if (type == null) {
            throw new BusinessException(DeliveryManagerErrorCode.DELIVERY_MANAGER_TYPE_REQUIRED);
        }
        if (type == DeliveryManagerType.COMPANY && hubId == null) {
            throw new BusinessException(DeliveryManagerErrorCode.COMPANY_DELIVERY_MANAGER_HUB_ID_REQUIRED);
        }

        validateSequence(sequence);
    }

    private static void validateSequence(int sequence) {
        if (sequence < MIN_SEQUENCE || sequence > MAX_SEQUENCE) {
            throw new BusinessException(DeliveryManagerErrorCode.DELIVERY_MANAGER_SEQUENCE_INVALID);
        }
    }
}
