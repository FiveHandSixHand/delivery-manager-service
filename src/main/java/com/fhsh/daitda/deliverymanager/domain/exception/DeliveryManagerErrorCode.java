package com.fhsh.daitda.deliverymanager.domain.exception;

import com.fhsh.daitda.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliveryManagerErrorCode implements ErrorCode {
    // internal
    USER_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 서비스 호출에 실패했습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "대상 사용자를 찾을 수 없습니다."),

    USER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "userId는 필수입니다."),
    SLACK_ID_REQUIRED(HttpStatus.BAD_REQUEST, "slackId는 필수입니다."),
    DELIVERY_MANAGER_TYPE_REQUIRED(HttpStatus.BAD_REQUEST, "배송 담당자 타입은 필수입니다."),
    COMPANY_DELIVERY_MANAGER_HUB_ID_REQUIRED(HttpStatus.BAD_REQUEST, "업체 배송 담당자는 허브 ID가 필요합니다."),

    DELIVERY_MANAGER_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "배송담당자는 최대 10명까지만 등록할 수 있습니다."),
    DELIVERY_MANAGER_SEQUENCE_INVALID(HttpStatus.BAD_REQUEST, "배송담당자 순번은 1~10 범위여야 합니다."),
    DELIVERY_MANAGER_ALREADY_DELIVERING(HttpStatus.BAD_REQUEST, "이미 배송 중인 담당자입니다."),
    DELETED_DELIVERY_MANAGER_CANNOT_CHANGE_STATUS(HttpStatus.BAD_REQUEST, "삭제된 배송 담당자는 배송 상태를 변경할 수 없습니다."),
    DELIVERY_MANAGER_NOT_DELIVERING(HttpStatus.BAD_REQUEST, "배송 중인 상태가 아닙니다."),

    DELIVERY_MANAGER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 배송담당자입니다.");

    private final HttpStatus status;
    private final String description;
}
