package com.fhsh.daitda.deliverymanager.application.service.command;

import com.fhsh.daitda.deliverymanager.application.client.UserClient;
import com.fhsh.daitda.deliverymanager.application.command.CreateDeliveryManagerCommand;
import com.fhsh.daitda.deliverymanager.application.command.UserInfoCommand;
import com.fhsh.daitda.deliverymanager.domain.entity.DeliveryManager;
import com.fhsh.daitda.deliverymanager.domain.enums.DeliveryManagerType;
import com.fhsh.daitda.deliverymanager.domain.repository.DeliveryManagerQueryRepository;
import com.fhsh.daitda.deliverymanager.domain.repository.DeliveryManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryManagerCommandService {

    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryManagerQueryRepository deliveryManagerQueryRepository;
    private final UserClient userClient;

    public void createDeliveryManager(CreateDeliveryManagerCommand command) {
        // user-service에서 사용자 정보 받아오기
        UserInfoCommand userInfo = userClient.getUser(command.targetUserId());

        // 사용자 정보 검증
        validateUser(userInfo, command);

        if (deliveryManagerQueryRepository.existsByUserId(userInfo.userId())) {
            throw new IllegalArgumentException("이미 등록된 배송담당자입니다.");
        }

        // 배송담당자 순번 지정
        Integer lastSequence = deliveryManagerQueryRepository.findLastSequence(
                command.type(), userInfo.hubId());
        int nextSequence = (lastSequence == null) ? 1 : lastSequence + 1;

        // 배송담당자 생성
        DeliveryManager deliveryManager = DeliveryManager.create(
                userInfo.userId(),
                userInfo.hubId(),
                userInfo.slackUserId(),
                command.type(),
                nextSequence
        );

        deliveryManagerRepository.save(deliveryManager);
    }

    private void validateUser(UserInfoCommand userInfo, CreateDeliveryManagerCommand command) {
        if (userInfo == null) {
            throw new IllegalArgumentException("대상 사용자를 찾을 수 없습니다.");
        }

        if (command.type() == DeliveryManagerType.COMPANY && userInfo.hubId() == null) {
            throw new IllegalArgumentException("업체 배송 담당자는 허브 정보가 필요합니다.");
        }

        if (userInfo.slackUserId() == null || userInfo.slackUserId().isBlank()) {
            throw new IllegalArgumentException("슬랙 ID가 없는 사용자입니다.");
        }
    }
}
