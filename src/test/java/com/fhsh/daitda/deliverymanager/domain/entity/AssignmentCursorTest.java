package com.fhsh.daitda.deliverymanager.domain.entity;

import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.exception.DeliveryManagerErrorCode;
import com.fhsh.daitda.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AssignmentCursorTest {

    @Test
    @DisplayName("마지막 배정 순번의 다음 번호 반환")
    void nextStartSequence() {
        AssignmentCursor cursor = AssignmentCursor.init(DeliveryManagerType.HUB, null);
        cursor.advanceTo(3);

        assertThat(cursor.nextStartSequence()).isEqualTo(4);
    }

    @Test
    @DisplayName("마지막 배정 순번이 10이면 다음 시작 순번은 1")
    void nextStartSequence_wrap() {
        AssignmentCursor cursor = AssignmentCursor.init(DeliveryManagerType.HUB, null);
        cursor.advanceTo(10);

        assertThat(cursor.nextStartSequence()).isEqualTo(1);
    }

    @Test
    @DisplayName("배정 순번이 1보다 작으면 예외 발생")
    void advanceTo_fail_whenLessThanMin() {
        AssignmentCursor cursor = AssignmentCursor.init(DeliveryManagerType.HUB, null);

        assertThatThrownBy(() -> cursor.advanceTo(0))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryManagerErrorCode.DELIVERY_MANAGER_SEQUENCE_INVALID);
    }

    @Test
    @DisplayName("배정 순번이 10보다 크면 예외 발생")
    void advanceTo_fail_whenGreaterThanMax() {
        AssignmentCursor cursor = AssignmentCursor.init(DeliveryManagerType.HUB, null);

        assertThatThrownBy(() -> cursor.advanceTo(11))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryManagerErrorCode.DELIVERY_MANAGER_SEQUENCE_INVALID);
    }

    @Test
    @DisplayName("업체 배송 담당자 커서는 허브 ID가 없으면 예외 발생")
    void init_fail_whenCompanyHubIdIsNull() {
        assertThatThrownBy(() -> AssignmentCursor.init(DeliveryManagerType.COMPANY, null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryManagerErrorCode.COMPANY_DELIVERY_MANAGER_HUB_ID_REQUIRED);
    }
}
