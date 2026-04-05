package com.fhsh.daitda.deliverymanager.application.service.command;

import com.fhsh.daitda.deliverymanager.application.client.UserLookupService;
import com.fhsh.daitda.deliverymanager.application.command.CompleteAssignmentCommand;
import com.fhsh.daitda.deliverymanager.application.command.CompleteCurrentDeliveryCommand;
import com.fhsh.daitda.deliverymanager.application.command.CreateDeliveryManagerCommand;
import com.fhsh.daitda.deliverymanager.application.command.UserInfoCommand;
import com.fhsh.daitda.deliverymanager.application.result.CompleteAssignmentResult;
import com.fhsh.daitda.deliverymanager.application.result.CompleteCurrentDeliveryResult;
import com.fhsh.daitda.deliverymanager.domain.entity.AssignmentCursor;
import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.exception.DeliveryManagerErrorCode;
import com.fhsh.daitda.deliverymanager.domain.repository.AssignmentCursorRepository;
import com.fhsh.daitda.deliverymanager.domain.repository.DeliveryManagerAssignmentRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeliveryManagerCommandServiceTest {

    @InjectMocks
    private DeliveryManagerCommandService commandService;


    @Mock
    private UserLookupService userLookupService;
    @Mock
    private DeliveryManagerRepository repository;
    @Mock
    private DeliveryManagerAssignmentRepository assignmentRepository;
    @Mock
    private AssignmentCursorRepository cursorRepository;

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

            given(userLookupService.getUser(targetUserId)).willReturn(userInfo);
            given(repository.existsByUserId(targetUserId)).willReturn(false);
            given(repository.findLastSequence(DeliveryManagerType.COMPANY, hubId)).willReturn(7); // 현재 마지막 순번 7번
            given(repository.save(any(DeliveryManager.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            commandService.createDeliveryManager(command);

            // then
            ArgumentCaptor<DeliveryManager> captor = ArgumentCaptor.forClass(DeliveryManager.class);
            verify(repository).save(captor.capture());

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

            given(userLookupService.getUser(targetUserId)).willReturn(userInfo);
            given(repository.existsByUserId(targetUserId)).willReturn(false);
            given(repository.findLastSequence(DeliveryManagerType.COMPANY, hubId)).willReturn(10); // 현재 마지막 순번 10번

            // when & then
            // 10명 생성 초과 오류 발생
            assertThatThrownBy(() -> commandService.createDeliveryManager(command))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(DeliveryManagerErrorCode.DELIVERY_MANAGER_LIMIT_EXCEEDED);

            verify(repository, never()).save(any(DeliveryManager.class));
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

            given(userLookupService.getUser(targetUserId)).willReturn(userInfo);
            // 해당 userId의 배송담당자가 이미 존재하므로 true 반환
            given(repository.existsByUserId(targetUserId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> commandService.createDeliveryManager(command))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(DeliveryManagerErrorCode.DELIVERY_MANAGER_ALREADY_EXISTS);

            verify(repository, never()).save(any(DeliveryManager.class));
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

            given(userLookupService.getUser(targetUserId)).willReturn(userInfo);

            // when & then
            assertThatThrownBy(() -> commandService.createDeliveryManager(command))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(DeliveryManagerErrorCode.COMPANY_DELIVERY_MANAGER_HUB_ID_REQUIRED);

            verify(repository, never()).save(any(DeliveryManager.class));
        }
    }

    @Nested
    @DisplayName("배송담당자 배송 완료")
    class CompleteCurrentDelivery {

        @Test
        @DisplayName("성공 케이스 - 배송 완료 성공")
        void completeCurrentDelivery_success() {
            // given
            UUID deliveryManagerId = UUID.randomUUID();
            UUID deliveryId = UUID.randomUUID();

            DeliveryManager deliveryManager = new DeliveryManager(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "exampleId",
                    DeliveryManagerType.HUB,
                    1,
                    true
            );

            CompleteCurrentDeliveryCommand command = new CompleteCurrentDeliveryCommand(deliveryId, deliveryManagerId);

            given(repository.findById(deliveryManagerId)).willReturn(Optional.of(deliveryManager));

            // when
            CompleteCurrentDeliveryResult result = commandService.completeCurrentDelivery(command);

            // then
            assertThat(result.deliveryId()).isEqualTo(deliveryId);
            assertThat(result.isDelivery()).isFalse();
            assertThat(deliveryManager.isDelivery()).isFalse();

            then(repository).should().findById(deliveryManagerId);
        }

        @Test
        @DisplayName("실패 케이스 - 배송중이 아닌 배송담당자가 요청하면 예외 발생")
        void completeCurrentDelivery_fail_notDelivering() {
            // given
            UUID deliveryManagerId = UUID.randomUUID();
            UUID deliveryId = UUID.randomUUID();

            DeliveryManager deliveryManager = new DeliveryManager(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "exampleId",
                    DeliveryManagerType.HUB,
                    1,
                    false
            );

            CompleteCurrentDeliveryCommand command = new CompleteCurrentDeliveryCommand(deliveryId, deliveryManagerId);

            given(repository.findById(deliveryManagerId)).willReturn(Optional.of(deliveryManager));

            // when
            Throwable thrown = catchThrowable(() -> commandService.completeCurrentDelivery(command));

            // then
            assertThat(thrown)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(DeliveryManagerErrorCode.DELIVERY_MANAGER_NOT_DELIVERING.getDescription());

            then(repository).should().findById(deliveryManagerId);
        }
    }

    @Nested
    @DisplayName("배송담당자 배정")
    class AssignmentDeliveryManager {
        @Test
        @DisplayName("성공 케이스 - 다음 담당자 배정, 커서 갱신")
        void completeAssignment_success_withExistingCursor() {
            // given
            UUID deliveryId = UUID.randomUUID();
            UUID hubId = UUID.randomUUID();

            CompleteAssignmentCommand command = new CompleteAssignmentCommand(deliveryId, hubId);

            AssignmentCursor cursor = AssignmentCursor.init(DeliveryManagerType.COMPANY, hubId);
            cursor.advanceTo(3); // 다음 시작 순번은 4

            // 배정 받을 4번 배송담당자 생성
            DeliveryManager deliveryManager = DeliveryManager.create(
                    UUID.randomUUID(),
                    hubId,
                    "exampleId",
                    DeliveryManagerType.COMPANY,
                    4
            );

            given(cursorRepository.findByTypeAndHubId(DeliveryManagerType.COMPANY, hubId))
                    .willReturn(Optional.of(cursor));

            given(assignmentRepository.findNextAssignable(DeliveryManagerType.COMPANY, hubId, 4))
                    .willReturn(Optional.of(deliveryManager));

            // when
            CompleteAssignmentResult result = commandService.completeAssignment(command);

            // then
            assertThat(result.deliveryManagerId()).isEqualTo(deliveryManager.getDeliveryManagerId());
            assertThat(cursor.getLastAssignedSequence()).isEqualTo(4);

            then(cursorRepository).should().findByTypeAndHubId(DeliveryManagerType.COMPANY, hubId);
            then(cursorRepository).should(never()).save(any(AssignmentCursor.class));
            then(assignmentRepository).should().findNextAssignable(DeliveryManagerType.COMPANY, hubId, 4);
        }

        @Test
        @DisplayName("성공 케이스 - 커서가 없으면 새 커서 생성, 첫 담당자 배정")
        void completeAssignment_success_withNewCursor() {
            // given
            UUID deliveryId = UUID.randomUUID();

            CompleteAssignmentCommand command = new CompleteAssignmentCommand(deliveryId, null);

            AssignmentCursor newCursor = AssignmentCursor.init(DeliveryManagerType.HUB, null);

            DeliveryManager deliveryManager = DeliveryManager.create(
                    UUID.randomUUID(),
                    null,
                    "exampleId",
                    DeliveryManagerType.HUB,
                    1
            );

            given(cursorRepository.findByTypeAndHubId(DeliveryManagerType.HUB, null))
                    .willReturn(Optional.empty());

            given(cursorRepository.save(any(AssignmentCursor.class)))
                    .willReturn(newCursor);

            given(assignmentRepository.findNextAssignable(DeliveryManagerType.HUB, null, 1))
                    .willReturn(Optional.of(deliveryManager));

            // when
            CompleteAssignmentResult result = commandService.completeAssignment(command);

            // then
            assertThat(result.deliveryManagerId()).isEqualTo(deliveryManager.getDeliveryManagerId());
            assertThat(newCursor.getLastAssignedSequence()).isEqualTo(1);

            then(cursorRepository).should().findByTypeAndHubId(DeliveryManagerType.HUB, null);
            then(cursorRepository).should().save(any(AssignmentCursor.class)); // 새 커서 만들어서 저장
            then(assignmentRepository).should().findNextAssignable(DeliveryManagerType.HUB, null, 1);
        }
    }

    // 자바 리플렉션 기능을 이용해 private 필드 값을 꺼냄
    private Object readField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }
}
