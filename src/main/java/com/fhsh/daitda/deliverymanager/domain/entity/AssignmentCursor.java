package com.fhsh.daitda.deliverymanager.domain.entity;

import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.exception.DeliveryManagerErrorCode;
import com.fhsh.daitda.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/*
 * 배송 담당자 순차 배정을 위해 마지막 배정 순번을 저장합니다.
 * 커서는 허브 배송 담당자 전체 그룹과 허브별 업체 배송 담당자 그룹 단위로 관리합니다.
 * 다음 배정 순번 계산 기능을 제공합니다.
 * 배정 완료 시 마지막 배정 순번을 갱신합니다.
 * 10번까지 배정을 완료하면 1번 순서로 돌아갑니다.
 * 동시 배정 요청 충돌 방지를 위해 버전 필드 또는 락 전략을 고려할 수 있습니다.
 * 업체 배송 담당자의 커서는 소속된 허브의 아이디를 필수로 갖고 있어야 합니다.
 */

@Entity
@Table(
        name = "p_assignment_cursor",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_assignment_cursor_type_hub", columnNames = {"type", "hub_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssignmentCursor {

    private static final int MIN_SEQUENCE = 1;
    private static final int MAX_SEQUENCE = 10;

    @Id
    @GeneratedValue
    private UUID cursorId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private DeliveryManagerType type;

    @Column(name = "hub_id")
    private UUID hubId;

    @Column(nullable = false)
    private int lastAssignedSequence;

    @Version
    private Long version;

    private AssignmentCursor(DeliveryManagerType type, UUID hubId, int lastAssignedSequence) {
        validate(type, hubId);
        this.type = type;
        this.hubId = hubId;
        this.lastAssignedSequence = lastAssignedSequence;
    }

    public static AssignmentCursor init(DeliveryManagerType type, UUID hubId) {
        return new AssignmentCursor(type, hubId, 0);
    }

    public int nextStartSequence() {
        return lastAssignedSequence == MAX_SEQUENCE ? MIN_SEQUENCE : lastAssignedSequence + 1;
    }

    public void advanceTo(int assignedSequence) {
        if (assignedSequence < MIN_SEQUENCE || assignedSequence > MAX_SEQUENCE) {
            throw new BusinessException(DeliveryManagerErrorCode.DELIVERY_MANAGER_SEQUENCE_INVALID);
        }
        this.lastAssignedSequence = assignedSequence;
    }

    private static void validate(DeliveryManagerType type, UUID hubId) {
        if (type == null) {
            throw new BusinessException(DeliveryManagerErrorCode.DELIVERY_MANAGER_TYPE_REQUIRED);
        }

        if (type == DeliveryManagerType.COMPANY && hubId == null) {
            throw new BusinessException(DeliveryManagerErrorCode.COMPANY_DELIVERY_MANAGER_HUB_ID_REQUIRED);
        }
    }
}
