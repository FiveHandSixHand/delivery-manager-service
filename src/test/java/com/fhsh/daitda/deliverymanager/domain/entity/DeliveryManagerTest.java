package com.fhsh.daitda.deliverymanager.domain.entity;

import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.exception.DeliveryManagerErrorCode;
import com.fhsh.daitda.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DeliveryManagerTest {

    @Test
    @DisplayName("배송 담당자 생성")
    void create() {
        UUID userId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();
        String slackId = "slack-123";

        DeliveryManager manager = DeliveryManager.create(
                userId, hubId, slackId, DeliveryManagerType.COMPANY, 3
        );

        assertThat(manager.getManagerInfo().getUserId()).isEqualTo(userId);
        assertThat(manager.getManagerInfo().getHubId()).isEqualTo(hubId);
        assertThat(manager.getManagerInfo().getSlackId()).isEqualTo(slackId);
        assertThat(manager.getType()).isEqualTo(DeliveryManagerType.COMPANY);
        assertThat(manager.getSequence()).isEqualTo(3);
        assertThat(manager.isDelivering()).isFalse();
        assertThat(manager.isDeleted()).isFalse();
        assertThat(manager.isAssignable()).isTrue();
    }

    @Test
    @DisplayName("업체 배송 담당자는 허브 ID가 없으면 생성 시 예외 발생")
    void create_fail_whenCompanyHubIdIsNull() {
        assertThatThrownBy(() ->
                DeliveryManager.create(
                        UUID.randomUUID(),
                        null,
                        "slack-123",
                        DeliveryManagerType.COMPANY,
                        1
                )
        ).isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(DeliveryManagerErrorCode.COMPANY_DELIVERY_MANAGER_HUB_ID_REQUIRED);
    }

    @Test
    @DisplayName("배송 시작 시 배송중 상태로 변경됨")
    void startDelivery() {
        DeliveryManager manager = DeliveryManager.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "slack-123",
                DeliveryManagerType.HUB,
                1
        );

        manager.startDelivery();

        assertThat(manager.isDelivering()).isTrue();
        assertThat(manager.isAssignable()).isFalse();
    }

    @Test
    @DisplayName("배송 완료 시 배송중 상태가 해제됨")
    void completeDelivery() {
        DeliveryManager manager = DeliveryManager.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "slack-123",
                DeliveryManagerType.HUB,
                1
        );
        manager.startDelivery();

        manager.completeDelivery();

        assertThat(manager.isDelivering()).isFalse();
        assertThat(manager.isAssignable()).isTrue();
    }
}
