package com.fhsh.daitda.deliverymanager.application.service.command;

import com.fhsh.daitda.deliverymanager.application.client.UserLookupService;
import com.fhsh.daitda.deliverymanager.application.command.CompleteAssignmentCommand;
import com.fhsh.daitda.deliverymanager.application.command.CompleteCurrentDeliveryCommand;
import com.fhsh.daitda.deliverymanager.application.command.CreateDeliveryManagerCommand;
import com.fhsh.daitda.deliverymanager.application.command.UserInfoCommand;
import com.fhsh.daitda.deliverymanager.application.result.CompleteAssignmentResult;
import com.fhsh.daitda.deliverymanager.application.result.CompleteCurrentDeliveryResult;
import com.fhsh.daitda.deliverymanager.application.result.CreateDeliveryManagerResult;
import com.fhsh.daitda.deliverymanager.application.result.DeleteDeliveryManagerResult;
import com.fhsh.daitda.deliverymanager.domain.entity.AssignmentCursor;
import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.exception.DeliveryManagerErrorCode;
import com.fhsh.daitda.deliverymanager.domain.repository.AssignmentCursorRepository;
import com.fhsh.daitda.deliverymanager.domain.repository.DeliveryManagerAssignmentRepository;
import com.fhsh.daitda.deliverymanager.domain.repository.DeliveryManagerRepository;
import com.fhsh.daitda.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryManagerCommandService {

    private final DeliveryManagerRepository deliveryManagerRepository;
    private final UserLookupService userLookupService;
    private final DeliveryManagerAssignmentRepository deliveryManagerAssignmentRepository;
    private final AssignmentCursorRepository assignmentCursorRepository;

    // 배송담당자 생성
    public CreateDeliveryManagerResult createDeliveryManager(CreateDeliveryManagerCommand command) {
        // user-service에서 사용자 정보 받아오기
        UserInfoCommand userInfo = userLookupService.getUser(command.targetUserId());

        // 사용자 정보 검증
        validateUser(userInfo, command);

        if (deliveryManagerRepository.existsByUserId(userInfo.userId())) {
            throw new BusinessException(DeliveryManagerErrorCode.DELIVERY_MANAGER_ALREADY_EXISTS);
        }

        // 배송담당자 순번 지정
        Integer lastSequence = deliveryManagerRepository.findLastSequence(
                command.type(), userInfo.hubId());
        int nextSequence = (lastSequence == null) ? 1 : lastSequence + 1;

        if (nextSequence > 10) {
            throw new BusinessException(DeliveryManagerErrorCode.DELIVERY_MANAGER_LIMIT_EXCEEDED);
        }

        // 배송담당자 생성
        DeliveryManager deliveryManager = DeliveryManager.create(
                userInfo.userId(),
                userInfo.hubId(),
                userInfo.slackUserId(),
                command.type(),
                nextSequence
        );

        DeliveryManager saved = deliveryManagerRepository.save(deliveryManager);

        return new CreateDeliveryManagerResult(saved.getDeliveryManagerId());
    }

    // 배송담당자 삭제
    public DeleteDeliveryManagerResult deleteDeliveryManager(UUID userId, UUID deliveryManagerId) {
        DeliveryManager deliveryManager = deliveryManagerRepository.findById(deliveryManagerId)
                .orElseThrow(() -> new BusinessException(DeliveryManagerErrorCode.DELIVERY_MANAGER_NOT_FOUND));

        deliveryManager.delete(userId);

        return new DeleteDeliveryManagerResult(deliveryManager.getDeliveryManagerId());
    }

    // 배송담당자 배송 완료
    public CompleteCurrentDeliveryResult completeCurrentDelivery(CompleteCurrentDeliveryCommand command) {
        DeliveryManager deliveryManager = deliveryManagerRepository.findByUserId(command.userId())
                .orElseThrow(() -> new BusinessException(DeliveryManagerErrorCode.USER_NOT_FOUND));

        // 배송 완료로 변경
        deliveryManager.completeDelivery(command.deliveryId());

        // 배송 완료 메시지 발행 필요

        return new CompleteCurrentDeliveryResult(command.deliveryId(), deliveryManager.isDelivery());
    }

    // 배송담당자 배정
    public CompleteAssignmentResult completeAssignment(CompleteAssignmentCommand command) {
        DeliveryManagerType type =
                (command.hubId() == null) ? DeliveryManagerType.HUB : DeliveryManagerType.COMPANY;

        // 지정된 타입과 허브에 맞는 커서 찾기
        // 커서가 없다면 생성/저장하여 반환
        AssignmentCursor cursor = assignmentCursorRepository.findByTypeAndHubId(type, command.hubId())
                .orElseGet(() -> assignmentCursorRepository.save(AssignmentCursor.init(type, command.hubId())));

        // 배정을 시작해야하는 시작 위치 받아오기
        int startSequence = cursor.nextStartSequence();

        // 지정된 타입과 허브에서 시작 위치부터 배정 순번 받아오기
        DeliveryManager deliveryManager = deliveryManagerAssignmentRepository
                .findNextAssignable(type, command.hubId(), startSequence)
                .orElseThrow(() -> new BusinessException(DeliveryManagerErrorCode.NO_AVAILABLE_DELIVERY_MANAGER));

        // 커서 갱신
        cursor.advanceTo(deliveryManager.getSequence());

        return CompleteAssignmentResult.from(deliveryManager, command.deliveryId());
    }

    private void validateUser(UserInfoCommand userInfo, CreateDeliveryManagerCommand command) {
        if (userInfo == null) {
            throw new BusinessException(DeliveryManagerErrorCode.USER_NOT_FOUND);
        }

        if (command.type() == DeliveryManagerType.COMPANY && userInfo.hubId() == null) {
            throw new BusinessException(DeliveryManagerErrorCode.COMPANY_DELIVERY_MANAGER_HUB_ID_REQUIRED);
        }

        if (userInfo.slackUserId() == null || userInfo.slackUserId().isBlank()) {
            throw new BusinessException(DeliveryManagerErrorCode.SLACK_ID_REQUIRED);
        }
    }
}
