package com.fhsh.daitda.deliverymanager.application.service.command;

import com.fhsh.daitda.deliverymanager.application.client.UserClient;
import com.fhsh.daitda.deliverymanager.application.command.CreateDeliveryManagerCommand;
import com.fhsh.daitda.deliverymanager.application.command.UserInfoCommand;
import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.exception.DeliveryManagerErrorCode;
import com.fhsh.daitda.deliverymanager.domain.repository.DeliveryManagerRepository;
import com.fhsh.daitda.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeliveryManagerCommandServiceTest {

    @InjectMocks
    private DeliveryManagerCommandService managerService;

    @Mock
    private DeliveryManagerRepository managerRepository;
    @Mock
    private UserClient userClient;

    @Nested
    @DisplayName("배송담당자 생성")
    class CreateDeliveryManager {

        @Test
        @DisplayName("정상 생성 케이스 - 마지막 순번에서 1 증가")
        void createDeliveryManager_success() throws Exception {
            // given
            UUID targetUserId = UUID.randomUUID();
            UUID hubId = UUID.randomUUID();

            // 예시 입력값
            CreateDeliveryManagerCommand command = new CreateDeliveryManagerCommand(targetUserId, DeliveryManagerType.COMPANY);

            // 예시 사용자 정보
            UserInfoCommand userInfo = new UserInfoCommand(targetUserId, hubId, "exampleId");

            given(userClient.getUser(targetUserId)).willReturn(userInfo);
            given(managerRepository.existsByUserId(targetUserId)).willReturn(false);
            given(managerRepository.findLastSequence(DeliveryManagerType.COMPANY, hubId)).willReturn(7); // 현재 마지막 순번 7번

            // when
            managerService.createDeliveryManager(command);

            // then
            ArgumentCaptor<DeliveryManager> captor = ArgumentCaptor.forClass(DeliveryManager.class);
            verify(managerRepository).save(captor.capture());

            // save 시점을 캡쳐한 결과 확인
            DeliveryManager saved = captor.getValue();
            assertThat(readField(saved, "sequence")).isEqualTo(8); // 생성 순번 8번
            assertThat(readField(saved, "type")).isEqualTo(DeliveryManagerType.COMPANY);
        }

        @Test
        @DisplayName("생성 실패 케이스 - 배송담당자 10명 이후 추가 생성")
        void createDeliveryManager_fail_LastSequenceIs10() {
            // given
            UUID targetUserId = UUID.randomUUID();
            UUID hubId = UUID.randomUUID();

            CreateDeliveryManagerCommand command = new CreateDeliveryManagerCommand(targetUserId, DeliveryManagerType.COMPANY);

            UserInfoCommand userInfo = new UserInfoCommand(targetUserId, hubId, "exampleId");

            given(userClient.getUser(targetUserId)).willReturn(userInfo);
            given(managerRepository.existsByUserId(targetUserId)).willReturn(false);
            given(managerRepository.findLastSequence(DeliveryManagerType.COMPANY, hubId)).willReturn(10); // 현재 마지막 순번 10번

            // when & then
            // 10명 생성 초과 오류 발생
            assertThatThrownBy(() -> managerService.createDeliveryManager(command))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(DeliveryManagerErrorCode.DELIVERY_MANAGER_LIMIT_EXCEEDED);

            verify(managerRepository, never()).save(any(DeliveryManager.class));
        }

        @Test
        @DisplayName("생성 실패 케이스 - 이미 등록된 배송담당자")
        void createDeliveryManager_fail_alreadyExists() {
            // given
            UUID targetUserId = UUID.randomUUID();
            UUID hubId = UUID.randomUUID();

            CreateDeliveryManagerCommand command =
                    new CreateDeliveryManagerCommand(targetUserId, DeliveryManagerType.COMPANY);

            UserInfoCommand userInfo =
                    new UserInfoCommand(targetUserId, hubId, "exampleId");

            given(userClient.getUser(targetUserId)).willReturn(userInfo);
            // 해당 userId의 배송담당자가 이미 존재하므로 true 반환
            given(managerRepository.existsByUserId(targetUserId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> managerService.createDeliveryManager(command))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(DeliveryManagerErrorCode.DELIVERY_MANAGER_ALREADY_EXISTS);

            verify(managerRepository, never()).save(any(DeliveryManager.class));
        }

        @Test
        @DisplayName("생성 실패 케이스 - 업체 배송 담당자의 허브 정보 누락")
        void createDeliveryManager_fail_companyWithoutHub() {
            // given
            UUID targetUserId = UUID.randomUUID();

            CreateDeliveryManagerCommand command =
                    new CreateDeliveryManagerCommand(targetUserId, DeliveryManagerType.COMPANY);

            UserInfoCommand userInfo =
                    new UserInfoCommand(targetUserId, null, "exampleId");

            given(userClient.getUser(targetUserId)).willReturn(userInfo);

            // when & then
            assertThatThrownBy(() -> managerService.createDeliveryManager(command))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(DeliveryManagerErrorCode.COMPANY_DELIVERY_MANAGER_HUB_ID_REQUIRED);

            verify(managerRepository, never()).save(any(DeliveryManager.class));
        }
    }

    // 자바 리플렉션 기능을 이용해 private 필드 값을 꺼냄
    private Object readField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }
}
