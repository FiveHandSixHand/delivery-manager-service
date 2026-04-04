package com.fhsh.daitda.deliverymanager.application.service.query;

import com.fhsh.daitda.deliverymanager.application.query.GetDeliveryManagerListQuery;
import com.fhsh.daitda.deliverymanager.application.query.GetDeliveryManagerQuery;
import com.fhsh.daitda.deliverymanager.application.query.GetMyDeliveryManagerQuery;
import com.fhsh.daitda.deliverymanager.application.result.GetDeliveryManagerListResult;
import com.fhsh.daitda.deliverymanager.application.result.GetDeliveryManagerResult;
import com.fhsh.daitda.deliverymanager.application.result.GetMyDeliveryManagerResult;
import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.repository.DeliveryManagerQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryManagerQueryServiceTest {

    @InjectMocks
    private DeliveryManagerQueryService deliveryManagerQueryService;

    @Mock
    private DeliveryManagerQueryRepository deliveryManagerQueryRepository;

    @DisplayName("성공 케이스 - 배송담당자 단건 조회")
    @Test
    void getDeliveryManager_success() {
        // given
        UUID deliveryManagerId = UUID.randomUUID();
        GetDeliveryManagerQuery query = new GetDeliveryManagerQuery(deliveryManagerId);

        DeliveryManager deliveryManager = DeliveryManager.builder()
                .userId(UUID.randomUUID())
                .slackId("exampleId")
                .type(DeliveryManagerType.HUB)
                .sequence(2)
                .isDelivery(false)
                .build();

        given(deliveryManagerQueryRepository.findById(deliveryManagerId)).willReturn(Optional.of(deliveryManager));

        // when
        GetDeliveryManagerResult result = deliveryManagerQueryService.getDeliveryManager(query);

        // then
        assertThat(result.userId()).isEqualTo(deliveryManager.getManagerInfo().getUserId());
        assertThat(result.type()).isEqualTo(DeliveryManagerType.HUB);
        assertThat(result.sequence()).isEqualTo(2);

        verify(deliveryManagerQueryRepository).findById(deliveryManagerId);
    }

    @DisplayName("성공 케이스 - 배송담당자 목록 조회")
    @Test
    void getDeliveryManagers_success() {
        // given
        GetDeliveryManagerListQuery query =
                new GetDeliveryManagerListQuery(DeliveryManagerType.COMPANY, null, "createdAt");

        Pageable pageable = PageRequest.of(0, 10);

        DeliveryManager deliveryManager = DeliveryManager.builder()
                .userId(UUID.randomUUID())
                .hubId(UUID.randomUUID())
                .slackId("exampleId")
                .type(DeliveryManagerType.COMPANY)
                .sequence(1)
                .isDelivery(false)
                .build();

        Page<DeliveryManager> deliveryManagerPage = new PageImpl<>(List.of(deliveryManager), pageable, 1);

        given(deliveryManagerQueryRepository.findAll(query, pageable)).willReturn(deliveryManagerPage);

        // when
        Page<GetDeliveryManagerListResult> result = deliveryManagerQueryService.getDeliveryManagers(query, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).userId()).isEqualTo(deliveryManager.getManagerInfo().getUserId());
        assertThat(result.getContent().get(0).type()).isEqualTo(DeliveryManagerType.COMPANY);

        verify(deliveryManagerQueryRepository).findAll(query, pageable);
    }

    @DisplayName("성공 케이스 - 배송담당자 본인 조회")
    @Test
    void getMyDeliveryManager_success() {
        // given
        UUID userId = UUID.randomUUID();
        GetMyDeliveryManagerQuery query = new GetMyDeliveryManagerQuery(userId);

        DeliveryManager deliveryManager = DeliveryManager.builder()
                .userId(userId)
                .hubId(UUID.randomUUID())
                .slackId("exampleId")
                .type(DeliveryManagerType.COMPANY)
                .sequence(3)
                .build();

        given(deliveryManagerQueryRepository.findByUserId(userId)).willReturn(Optional.of(deliveryManager));

        // when
        GetMyDeliveryManagerResult result = deliveryManagerQueryService.getMyDeliveryManager(query);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.type()).isEqualTo(DeliveryManagerType.COMPANY);
        assertThat(result.sequence()).isEqualTo(3);

        verify(deliveryManagerQueryRepository).findByUserId(userId);
    }
}
