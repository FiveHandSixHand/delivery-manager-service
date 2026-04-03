package com.fhsh.daitda.deliverymanager.domain.exception;

import com.fhsh.daitda.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliveryManagerErrorCode implements ErrorCode {
    USER_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 서비스 호출에 실패했습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "대상 사용자를 찾을 수 없습니다."),
    DELIVERY_MANAGER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 배송담당자입니다."),
    DELIVERY_MANAGER_HUB_REQUIRED(HttpStatus.BAD_REQUEST, "업체 배송 담당자는 허브 정보가 필요합니다."),
    DELIVERY_MANAGER_SLACK_ID_REQUIRED(HttpStatus.BAD_REQUEST, "슬랙 ID가 없는 사용자입니다."),
    DELIVERY_MANAGER_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "배송담당자는 최대 10명까지만 등록할 수 있습니다.");

    private final HttpStatus status;
    private final String description;
}
